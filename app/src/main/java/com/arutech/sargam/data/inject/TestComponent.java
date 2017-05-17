package com.arutech.sargam.data.inject;

import com.arutech.sargam.lastfm.data.inject.LastFmModule;
import com.arutech.sargam.saavn.data.inject.SaavnModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, TestPlayerModule.class, DemoModule.class, LastFmModule.class, SaavnModule.class})
public interface TestComponent extends SargamGraph {
}
