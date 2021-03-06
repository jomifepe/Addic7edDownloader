package com.jomifepe.addic7eddownloader.util;

public final class Const {

    public static final int RC_WRITE_EXTERNAL_STORAGE = 724;
    public static final String DATABASE_FILENAME = "addic7edd.db";
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36";
    public static final String NOTIFICATION_CHANNEL = "addic7edd";

    public final static class Activity {
        public static final String EXTRA_SHOW = "com.jomifepe.addic7eddownloader.ui.SHOW";
        public static final String EXTRA_MEDIA = "com.jomifepe.addic7eddownloader.ui.MEDIA";
        public static final String EXTRA_CONTENT = "com.jomifepe.addic7eddownloader.ui.EPISODE";
    }

    /**
     * Addic7ed specific constants
     */
    public final static class Addic7ed {

        public static final String BASE_URL = "http://www.addic7ed.com/";
        public static final String SEARCH_URL = BASE_URL + "search.php?search=%s&Submit=SearchRequest";
        public static final String TVSHOWS_URL = BASE_URL + "shows.php";
        public static final String TVSHOW_PAGE_URL = BASE_URL + "show/%d";
        public static final String TVSHOW_SEASON_EPISODES_URL = BASE_URL + "ajax_loadShow.php?show=%d&season=%d";
        /* showname/S#/E#/epname */
        public static final String TVSHOW_EPISODE_SUBTITLES_URL = BASE_URL + "serie/%s/%d/%d/%s";

        public static final String SM_HEADER_CONTAINER_CLASS = ".container";
        public static final String SM_HEADER_TABLE_CLASS = ".tabel70 .tabel95 tr:nth-child(2) a";

        public static final String ID_SHOWS_SELECT = "#qsShow";
        public static final String S_SHOWS_SELECT_OPTIONS = ID_SHOWS_SELECT + " option";
        public static final String ID_SEASON_EPISODES_SELECT = "#qsiEp";
        public static final String S_SHOW_EPISODES_SELECT_OPTIONS = ID_SEASON_EPISODES_SELECT + " option";
        public static final String ID_SHOW_SEASONS_SELECT = "#qsiSeason";
        public static final String S_SHOW_SEASONS_SELECT_OPTIONS = ID_SHOW_SEASONS_SELECT + " option";
        public static final String CLASS_MEDIA_PAGE_TITLE = ".titulo";

        public static final String TVSHOWS_TABLE_CLASS = ".tabel90";
        public static final String TVSHOWS_INFO_CELL_CLASS = ".version";
        public static final String TVSHOWS_INFO_CELL = TVSHOWS_TABLE_CLASS + " " + TVSHOWS_INFO_CELL_CLASS;
        public static final String TVSHOWS_SE_CELL_CLASS = ".newsDate";
        public static final String TVSHOWS_SE_CELL = TVSHOWS_TABLE_CLASS  + " " + TVSHOWS_SE_CELL_CLASS;
        public static final String TVSHOWS_TITLE_CELL_CLASS = ".aztitle";

        public static final String TVSHOW_PAGE_SEASON_BUTTONS_DIV = "#sl";
        public static final String TVSHOW_SEASON_EPISODES_TABLE_ROWS = "#season tbody tr";

        public static final String S_SEARCH_RESULTS_TABLE = ".tabel > tbody";
        public static final String S_SEARCH_RESULTS_TABLE_ROWS = ".tabel > tbody tr";
        public static final String SM_SUB_TABLE_CLASS = ".tabel95 .tabel95";
        public static final String SM_VERSION_ELEM_CLASS = ".NewsTitle";
        public static final String SM_INFO_ELEM_CLASS = ".newsDate";
        public static final String SM_LANGUAGE_ELEM_CLASS = ".language";
        public static final String SM_IMAGE_TITLE_CORRECTED = "Corrected";
        public static final String SM_IMAGE_TITLE_HEARING_IMPAIRED = "Hearing Impaired";
        public static final String SM_IMAGE_TITLE_HD = "720/1080";
        public static final String SM_IMAGE_HDICON_FILENAME = "hdicon.png";
        public static final String SM_BUTTON_DOWNLOAD_CLASS = ".buttonDownload";

    }
    public final static class File {
        public static final String DEFAULT_ERROR_LOG_FILENAME = "addic7edd.error.log";
    }

    public final static class TMDB {
        public static final String API_KEY_V3 = "27702f41ed36c7fe12af78910041c736";
        public static final String BASE_URL = "https://api.themoviedb.org";
        public static final String IMAGE_BASE_PATH = "http://image.tmdb.org";
        public static final String IMAGE_PATH_W185 = IMAGE_BASE_PATH + "/t/p/w185/%s";
        public static final String SHOW_URL = BASE_URL + "/3/tv/%s?api_key=%s&language=%s";
        public static final String SHOW_SEARCH_URL = BASE_URL + "/3/search/tv?query=%s&api_key=%s&language=%s";
    }
}
