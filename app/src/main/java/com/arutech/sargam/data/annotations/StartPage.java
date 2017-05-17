package com.arutech.sargam.data.annotations;

import android.support.annotation.IntDef;

@IntDef(value = {StartPage.SONGS, StartPage.ALBUMS, StartPage.ARTISTS})
public @interface StartPage {
    int SONGS = 0;
    int ALBUMS = 1;
    int ARTISTS = 2;
}
