package com.jomifepe.addic7eddownloader;

import android.os.Handler;
import android.util.Pair;

import com.jomifepe.addic7eddownloader.model.Episode;
import com.jomifepe.addic7eddownloader.model.Record;
import com.jomifepe.addic7eddownloader.model.Season;
import com.jomifepe.addic7eddownloader.model.Subtitle;
import com.jomifepe.addic7eddownloader.model.TVShow;
import com.jomifepe.addic7eddownloader.util.AsyncUtil;
import com.jomifepe.addic7eddownloader.util.Const;
import com.jomifepe.addic7eddownloader.util.NetworkUtil;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class Addic7ed {
    public interface RecordResultListener<T extends Record> {
        void onComplete(List<T> records);
        void onFailure(Exception e);
    }

    public static LinkedList<Subtitle> searchSubtitle(String parameters) throws UnsupportedEncodingException {
        LinkedList<Subtitle> subs = new LinkedList<>();
        String searchURL = String.format(Const.Addic7ed.SEARCH_URL, URLEncoder.encode(parameters, "UTF-8"));
        try {
            NetworkUtil.OkHTTPGETRequest resultsRequest =
                    new NetworkUtil.OkHTTPGETRequest(searchURL)
                    .addOnCompleteListener(result -> {
                        LinkedList<URL> resultsURLs = null;
                        try {
                            resultsURLs = parseSearchResults(result);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        }

                        if (!result.isEmpty()) {
                            for (URL resultURL : resultsURLs) {
                                NetworkUtil.OkHTTPGETRequest subtitlesRequest =
                                        new NetworkUtil.OkHTTPGETRequest(resultURL.toString())
                                        .addOnCompleteListener(subtitlesResult -> {
//                                            LinkedList<Subtitle> parsedSubtitles = parseEpisodeSubtitlesDocument(response,
//                                            resultURL.toString().contains("serie/") ? MediaType.TV_SHOW : MediaType.MOVIE);
//                                            LinkedList<Subtitle> parsedSubtitles = parseEpisodeSubtitlesDocument(response);
//                                            subs.addAll(parsedSubtitles);
                                        });
                                subtitlesRequest.execute();
                            }
                        }
                    });
            resultsRequest.execute();
        } catch (Exception e) {
            System.err.println(String.format("%s is not a valid URL.", searchURL));
        }

        return subs;
    }

    public static void getTVShows(RecordResultListener<TVShow> resultListener) {
        try {
            NetworkUtil.NetworkTaskCompleteListener onComplete = result -> {
                AsyncUtil.RunnableAsyncTask parsingTask = new AsyncUtil.RunnableAsyncTask(() -> {
                    ArrayList<TVShow> parsedResult = parseTVShowsSelectOptionElement(result);
                    resultListener.onComplete(parsedResult);
                });
                parsingTask.execute();
            };

            NetworkUtil.OkHTTPGETRequest request = new NetworkUtil.OkHTTPGETRequest(Const.Addic7ed.BASE_URL);
            request.addOnFailureListener(resultListener::onFailure);
            request.addOnCompleteListener(onComplete);
            request.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getTVShowSeasons(TVShow show, RecordResultListener<Season> resultListener) {
        try {
            NetworkUtil.NetworkTaskCompleteListener onComplete = result -> {
                AsyncUtil.RunnableAsyncTask parsingTask = new AsyncUtil.RunnableAsyncTask(() -> {
                    ArrayList<Season> parsedResults = parseSeasonsFromShowDocument(show, result);
                    resultListener.onComplete(parsedResults);
                });
                parsingTask.execute();
            };

            String url = String.format(Locale.getDefault(),
                    Const.Addic7ed.TVSHOW_PAGE_URL, show.getAddic7edId());
            NetworkUtil.OkHTTPGETRequest request = new NetworkUtil.OkHTTPGETRequest(url);
            request.addOnFailureListener(resultListener::onFailure);
            request.addOnCompleteListener(onComplete);
            request.execute();
        } catch (Exception e) {
            resultListener.onFailure(e);
        }
    }

    public static void getSeasonEpisodes(TVShow show, Season season,
                                         RecordResultListener<Episode> resultListener) {
        try {
            NetworkUtil.NetworkTaskCompleteListener onComplete = result -> {
                AsyncUtil.RunnableAsyncTask parsingTask = new AsyncUtil.RunnableAsyncTask(() -> {
                    ArrayList<Episode> parsedResults = parseSeasonDocument(result, season);
                    resultListener.onComplete(parsedResults);
                });
                parsingTask.execute();
            };

            String url = String.format(Locale.getDefault(), Const.Addic7ed.TVSHOW_SEASON_EPISODES_URL,
                    show.getAddic7edId(), season.getNumber());
            NetworkUtil.OkHTTPGETRequest request = new NetworkUtil.OkHTTPGETRequest(url);
            request.addOnFailureListener(resultListener::onFailure);
            request.addOnCompleteListener(onComplete);
            request.execute();
        } catch (Exception e) {
            resultListener.onFailure(e);
        }
    }

    public static void getEpisodeSubtitles(Episode episode, RecordResultListener<Subtitle> resultListener) {
        try {
            NetworkUtil.NetworkTaskCompleteListener onComplete = result -> {
                AsyncUtil.RunnableAsyncTask parsingTask = new AsyncUtil.RunnableAsyncTask(() -> {
                    LinkedList<Subtitle> parsedResults = parseEpisodeSubtitlesDocument(result, episode);
                    resultListener.onComplete(parsedResults);
                });
                parsingTask.execute();
            };

            NetworkUtil.OkHTTPGETRequest request = new NetworkUtil.OkHTTPGETRequest(episode.getPageURL());
            request.addOnFailureListener(resultListener::onFailure);
            request.addOnCompleteListener(onComplete);
            request.execute();
        } catch (Exception e) {
            resultListener.onFailure(e);
        }
    }

    private static Integer getNumberOfEpisodesFromSeasonDocument(String document) {
        Document parsedDocument = Jsoup.parse(document);
        Elements episodesTableRows = parsedDocument.select(Const.Addic7ed.TVSHOW_SEASON_EPISODES_TABLE_ROWS);
        Elements episodeSeparator = episodesTableRows.select("tr[height=\"2\"]");

        return episodeSeparator.size() + 1;
    }

//    private static void parseSeasonsFromShowDocumentWithNumberOfEpisode(TVShow show,
//         String document, RecordResultListener resultListener)
//            throws ExecutionException, InterruptedException {
//
//        List<Season> resultList = new ArrayList<>();
//        Document parsePage = Jsoup.parse(document);
//        Elements seasonButtons = parsePage.select(String.format(Locale.getDefault(),
//                "%s button", Const.Addic7ed.TVSHOW_PAGE_SEASON_BUTTONS_DIV));
//
//        for (Element button : seasonButtons) {
//            String season = button.text();
//            int seasonNumber = Integer.parseInt(season);
//
//            String url = String.format(Locale.getDefault(),
//                    Const.Addic7ed.TVSHOW_SEASON_EPISODES_URL, show.getAddic7edId(), seasonNumber);
//            NetworkUtil.OkHTTPGETRequest request = new NetworkUtil.OkHTTPGETRequest(url);
//            request.addOnFailureListener(resultListener::onFailure);
//            request.addOnCompleteListener(result -> {
//                Integer numberOfEpisodes = getNumberOfEpisodesFromSeasonDocument(result);
//                resultList.add(new Season(show.getAddic7edId(), seasonNumber, numberOfEpisodes));
//            });
//            request.execute();
//        }
//    }

    private static ArrayList<Season> parseSeasonsFromShowDocument(TVShow show, String document) {
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

    private static ArrayList<Episode> parseSeasonDocument(String document, Season season) throws NumberFormatException {
        ArrayList<Episode> results = new ArrayList<>();

        Document page = Jsoup.parse(document);
        Elements subRows = page.select(Const.Addic7ed.TVSHOW_SEASON_EPISODES_TABLE_ROWS);

        String title, auxTitle = "";
        for (Element subRow : subRows) {
            Elements a = subRow.select("td:nth-of-type(3) > a");
            title = a.text();
            if (!StringUtil.isBlank(title) && !title.equals(auxTitle)) {
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

    private static ArrayList<TVShow> parseTVShowsSelectOptionElement(String document) {
        ArrayList<TVShow> results = new ArrayList<>();

        Document parsedDocument = Jsoup.parse(document);
        Elements select = parsedDocument.select("#qsShow option");

        for (Element option : select) {
            String val = option.val();
            int id = Integer.parseInt(val);
            if (id == 0) continue;
            String title = option.text();
            results.add(new TVShow(id, title));
        }

        return results;
    }

    private static ArrayList<TVShow> parseTVShowsDocument(String document) {
        ArrayList<TVShow> results = new ArrayList<>();

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

                TVShow tvShow = new TVShow(id, title);
                results.add(tvShow);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Parses the whole page fetched from searching for a subtitle
     * @param document
     * @return LinkedList with the URL of each search result
     */
    private static LinkedList<URL> parseSearchResults(String document) throws MalformedURLException {
        LinkedList<URL> results = new LinkedList<>();

        Document page = Jsoup.parse(document);
        Elements tableRows = page.select(Const.Addic7ed.SEARCH_RESULTS_TABLE_CLASS + "> tbody");

        for (Element row : tableRows) {
            Elements rowCells = row.select("td:nth-child(2)");
            for (Element cell : rowCells) {
                Elements a = cell.select("a");
                String href = a.attr("href");
                results.add(new URL(Const.Addic7ed.BASE_URL + href));
            }
        }

        return results;
    }

    /**
     * Parses the whole page of a single movie or tv show episode
     * @param document
     * @return LinkedList with each subtitle found on the page
     */
    private static LinkedList<Subtitle> parseEpisodeSubtitlesDocument(String document, Episode episode) {
        LinkedList<Subtitle> results = new LinkedList<>();

        Document smPage = Jsoup.parse(document);
//        Elements titleSpan = smPage.select(Const.Addic7ed.SM_MEDIA_TITLE);
//        String title = titleSpan.text().split("<span>")[0].replaceAll("[\\-,:; ]+", ".");
        Elements subs = smPage.select(Const.Addic7ed.SM_SUB_TABLE_CLASS);

        for (Element sub : subs) {
            Element cellNewsTitle = sub.select(Const.Addic7ed.SM_VERSION_ELEM_CLASS).get(0);
            Elements hdIconsList = cellNewsTitle.select(String.format("[title=\"%s\"]", Const.Addic7ed.SM_IMAGE_TITLE_HD));

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
                Elements correctedIconsList = subtitleInfoCell.select(String.format("[title=\"%s\"]", Const.Addic7ed.SM_IMAGE_TITLE_CORRECTED));
                Elements hearingImpairedIconsList = subtitleInfoCell.select(String.format("[title=\"%s\"]", Const.Addic7ed.SM_IMAGE_TITLE_HEARING_IMPAIRED));
                Elements downloadButton = subtitleRow.select(Const.Addic7ed.SM_BUTTON_DOWNLOAD_CLASS);

                String language = languageCell.text();
                Boolean isCorrected = correctedIconsList.size() != 0;
                Boolean isHearingImpaired = hearingImpairedIconsList.size() != 0;
                String downloadURL = String.format("%s%s", Const.Addic7ed.BASE_URL, downloadButton.get(0).attr("href"));
                if (downloadButton.size() > 1) {
                    for (Element button : downloadButton) {
                        if (button.attr("href").contains("updated")) {
                            downloadURL = String.format("%s%s", Const.Addic7ed.BASE_URL, button.attr("href"));
                            break;
                        }
                    }
                }

                results.add(new Subtitle(episode.getId(), language, version, isCorrected, isHearingImpaired, isHD, downloadURL));
            }
        }

        return results;
    }
}
