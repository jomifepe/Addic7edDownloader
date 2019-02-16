package com.jomifepe.addic7eddownloader.api;

import android.os.AsyncTask;

import com.jomifepe.addic7eddownloader.model.Content;
import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.model.MediaType;
import com.jomifepe.addic7eddownloader.model.SearchResult;
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.model.Show;
import com.jomifepe.addic7eddownloader.model.Subtitle;
import com.jomifepe.addic7eddownloader.util.AsyncTaskResult;
import com.jomifepe.addic7eddownloader.util.Const;
import com.jomifepe.addic7eddownloader.util.Util;
import com.jomifepe.addic7eddownloader.util.listener.OnFailureListener;
import com.jomifepe.addic7eddownloader.util.listener.OnResultListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public abstract class Addic7ed<T> extends AsyncTask<Void, Void, AsyncTaskResult<T>> {
    private OnResultListener<T> resultListener;
    private OnFailureListener failureListener;

    public Addic7ed(OnResultListener<T> resultListener, OnFailureListener failureListener) {
        this.resultListener = resultListener;
        this.failureListener = failureListener;
    }

    @Override
    protected AsyncTaskResult<T> doInBackground(Void... voids) {
        try {
            return request();
        } catch (IOException e) {
            return new AsyncTaskResult<>(e);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<T> result) {
        if (result.hasError()) {
            failureListener.onFailure(result.getError());
        } else {
            resultListener.onComplete(result.getResult());
        }
    }

    protected abstract AsyncTaskResult<T> request() throws IOException;

    public static class SearchRequest extends Addic7ed<List<SearchResult>> {
        private String query;

        public SearchRequest(String query, OnResultListener<List<SearchResult>> successListener,
                             OnFailureListener failureListener) {
            super(successListener, failureListener);
            this.query = query;
        }

        @Override
        public AsyncTaskResult<List<SearchResult>> request() throws IOException {
            String searchURL = String.format(Const.Addic7ed.SEARCH_URL, URLEncoder.encode(query, "UTF-8"));
            String result = new Util.Network.HTTPGETRequest(searchURL).execute();
            return new AsyncTaskResult<>(parseSearchResults(result));
        }

        private LinkedList<SearchResult> parseSearchResults(String document) {
            LinkedList<SearchResult> results = new LinkedList<>();

            Document page = Jsoup.parse(document);
            Elements tableRows = page.select(Const.Addic7ed.S_SEARCH_RESULTS_TABLE_ROWS);
            for (Element row : tableRows) {
                Elements anchor = row.select("td:nth-child(2)").select("a");

                /* ignoring the empty cells */
                if (!anchor.isEmpty()) {
                    String description = anchor.text();
                    String href = anchor.attr("href");
                    String url = Const.Addic7ed.BASE_URL + href;
                    if (description != null && href != null) {
                        MediaType type = href.contains("serie/") ? MediaType.SHOW : MediaType.MOVIE;
                        results.add(new SearchResult(description, type, url));
                    }
                }
            }

            return results;
        }
    }

    public static class ShowsRequest extends Addic7ed<List<Show>> {
        public ShowsRequest(OnResultListener<List<Show>> resultListener,
                            OnFailureListener failureListener) {
            super(resultListener, failureListener);
        }

        @Override
        protected AsyncTaskResult<List<Show>> request() throws IOException {
            String result = new Util.Network.HTTPGETRequest(Const.Addic7ed.BASE_URL).execute();
            return new AsyncTaskResult<>(parseTVShowsSelectOptionElement(result));
        }

        private List<Show> parseTVShowsSelectOptionElement(String document) {
            List<Show> results = new ArrayList<>();

            Document parsedDocument = Jsoup.parse(document);
            Elements select = parsedDocument.select(Const.Addic7ed.S_SHOWS_SELECT_OPTIONS);

            for (Element option : select) {
                int id = Integer.parseInt(option.val());
                /* ignore the default option */
                if (id == 0) {
                    continue;
                }

                String title = option.text();
                results.add(new Show(id, title));
            }

            return results;
        }

        private List<Show> parseShowsPage(String document) {
            ArrayList<Show> results = new ArrayList<>();

            Document page = Jsoup.parse(document);
            Elements tableCells = page.select(Const.Addic7ed.TVSHOWS_TABLE_CLASS + " td");

            Elements cellsShow = tableCells.select(Const.Addic7ed.TVSHOWS_INFO_CELL_CLASS);
            Elements cellsShowSE = tableCells.select(Const.Addic7ed.TVSHOWS_SE_CELL_CLASS);

            try {
                for (int i = 0; i < cellsShow.size(); i++) {

                    Element cell = cellsShow.get(i);
                    Elements a = cellsShow.get(i).select("a");

                    String title = a.text();
                    Integer id = null,
                            numberOfSeasons = null,
                            numberOfEpisodes = null;
                    try {
                        String digitRegex = "\\D+";

                        id = Integer.parseInt(a.attr("href").replaceAll(digitRegex, ""));
                        String[] se = cellsShowSE.get(i).text().split(",");
                        numberOfSeasons = Integer.parseInt(se[0].replaceAll(digitRegex, ""));
                        numberOfEpisodes = Integer.parseInt(se[1].replaceAll(digitRegex, ""));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    String imageURL = cell.select("img").attr("src");

                    Show show = new Show(id, title);
                    results.add(show);
                }

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            return results;
        }
    }

    public static class ShowSeasonsRequest extends Addic7ed<List<Season>> {
        private Show show;

        public ShowSeasonsRequest(Show show,
                                  OnResultListener<List<Season>> result,
                                  OnFailureListener failureListener) {
            super(result, failureListener);
            this.show = show;
        }

        @Override
        protected AsyncTaskResult<List<Season>> request() throws IOException {
            String url = String.format(Locale.getDefault(),
                    Const.Addic7ed.TVSHOW_PAGE_URL, show.getAddic7edId());
            String result = new Util.Network.HTTPGETRequest(url).execute();
            return new AsyncTaskResult<>(parseSeasonsFromShowDocument(show, result));
        }

        private static ArrayList<Season> parseSeasonsFromShowDocument(Show show, String document) {
            ArrayList<Season> resultList = new ArrayList<>();
            Document parsePage = Jsoup.parse(document);
            Elements seasonButtons = parsePage.select(String.format(Locale.getDefault(),
                    "%s button", Const.Addic7ed.TVSHOW_PAGE_SEASON_BUTTONS_DIV));

            for (Element button : seasonButtons) {
                int seasonNumber = Integer.parseInt(button.text());
                resultList.add(new Season(show.getAddic7edId(), seasonNumber, null));
            }

            return resultList;
        }

        private Integer getNumberOfEpisodesFromSeasonDocument(String document) {
            Document parsedDocument = Jsoup.parse(document);
            Elements episodesTableRows = parsedDocument.select(Const.Addic7ed.TVSHOW_SEASON_EPISODES_TABLE_ROWS);
            Elements episodeSeparator = episodesTableRows.select("tr[height=\"2\"]");

            return episodeSeparator.size() + 1;
        }
    }

    public static class SeasonEpisodesRequest extends Addic7ed<List<Episode>> {
        private final Show show;
        private final Season season;

        public SeasonEpisodesRequest(Show show, Season season,
                                     OnResultListener<List<Episode>> resultListener,
                                     OnFailureListener failureListener) {
            super(resultListener, failureListener);
            this.show = show;
            this.season = season;
        }

        @Override
        protected AsyncTaskResult<List<Episode>> request() throws IOException {
            String url = String.format(Locale.getDefault(), Const.Addic7ed.TVSHOW_SEASON_EPISODES_URL,
                    show.getAddic7edId(), season.getNumber());
            String result = new Util.Network.HTTPGETRequest(url).execute();
            return new AsyncTaskResult<>(parseSeasonDocument(result));
        }

        private List<Episode> parseSeasonDocument(String document) throws NumberFormatException {
            ArrayList<Episode> results = new ArrayList<>();

            Document page = Jsoup.parse(document);
            Elements subRows = page.select(Const.Addic7ed.TVSHOW_SEASON_EPISODES_TABLE_ROWS);

            String title, auxTitle = "";
            for (Element subRow : subRows) {
                Elements a = subRow.select("td:nth-of-type(3) > a");
                title = a.text();
                if (!title.isEmpty() && !title.equals(auxTitle)) {
                    auxTitle = title;
                    String episodePageURL = a.attr("href");
                    Integer seasonNumber = Integer.parseInt(subRow.select("td:nth-of-type(1)").text());
                    Integer episodeNumber = Integer.parseInt(subRow.select("td:nth-of-type(2)").text());
                    results.add(new Episode(season.getId(), title, seasonNumber, episodeNumber,
                            String.format("%s%s", Const.Addic7ed.BASE_URL, episodePageURL)));
                }
            }

            return results;
        }
    }

    public static class ContentSubtitlesRequest extends Addic7ed<List<Subtitle>> {
        private final Content content;

        public ContentSubtitlesRequest(Content content,
                                       OnResultListener<List<Subtitle>> resultListener,
                                       OnFailureListener failureListener) {
            super(resultListener, failureListener);
            this.content = content;
        }

        @Override
        protected AsyncTaskResult<List<Subtitle>> request() throws IOException {
            String result = new Util.Network.HTTPGETRequest(content.getPageUrl()).execute();
            return new AsyncTaskResult<>(parseEpisodeSubtitlesDocument(result));
        }

        /**
         * Parses the whole page of a single movie or tv show episode
         * @param document
         * @return LinkedList with each subtitle found on the page
         */
        private List<Subtitle> parseEpisodeSubtitlesDocument(String document) {
            LinkedList<Subtitle> results = new LinkedList<>();

            Document smPage = Jsoup.parse(document);
//        Elements titleSpan = smPage.select(Const.Addic7ed.CLASS_MEDIA_PAGE_TITLE);
//        String title = titleSpan.text().split("<span>")[0].replaceAll("[\\-,:; ]+", ".");
            Elements subs = smPage.select(Const.Addic7ed.SM_SUB_TABLE_CLASS);

            for (Element sub : subs) {
                Element cellNewsTitle = sub.select(Const.Addic7ed.SM_VERSION_ELEM_CLASS).get(0);
                Elements hdIconsList = cellNewsTitle.select(String.format("[title=\"%s\"]",
                        Const.Addic7ed.SM_IMAGE_TITLE_HD));

                String version = cellNewsTitle.text().split(",")[0].split(" ")[1];
                Boolean isHD = hdIconsList.size() != 0;

                Elements languageCells = sub.select(Const.Addic7ed.SM_LANGUAGE_ELEM_CLASS);
                for (Element languageCell : languageCells) {
                    /* row where the language, completed tag and download buttons are */
                    Element subtitleRow = languageCell.parent();
                    /* cell where the hd, hi and corrected icons are */
                    Element subtitleRowSibling = subtitleRow.nextElementSibling();
                    Element subtitleInfoCell = subtitleRowSibling.select(Const.Addic7ed.SM_INFO_ELEM_CLASS).get(0);

                    /* auxiliary elements */
                    Elements correctedIconsList = subtitleInfoCell.select(
                            String.format("[title=\"%s\"]", Const.Addic7ed.SM_IMAGE_TITLE_CORRECTED));
                    Elements hearingImpairedIconsList = subtitleInfoCell.select(
                            String.format("[title=\"%s\"]", Const.Addic7ed.SM_IMAGE_TITLE_HEARING_IMPAIRED));
                    Elements downloadButton = subtitleRow.select(Const.Addic7ed.SM_BUTTON_DOWNLOAD_CLASS);

                    String language = languageCell.text();
                    Boolean isCorrected = correctedIconsList.size() != 0;
                    Boolean isHearingImpaired = hearingImpairedIconsList.size() != 0;
                    String downloadURL = String.format("%s%s", Const.Addic7ed.BASE_URL,
                            downloadButton.get(0).attr("href"));
                    if (downloadButton.size() > 1) {
                        for (Element button : downloadButton) {
                            if (button.attr("href").contains("updated")) {
                                downloadURL = String.format("%s%s", Const.Addic7ed.BASE_URL,
                                        button.attr("href"));
                                break;
                            }
                        }
                    }

                    Integer id = content instanceof Episode ? ((Episode) content).getId() : null;
                    results.add(new Subtitle(id, language, version, isCorrected,
                            isHearingImpaired, isHD, downloadURL));
                }
            }

            return results;
        }
    }
}
