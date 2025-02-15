
/*
 * Copyright 2010 Mario Zechner (contact@badlogicgames.com), Nathan Sweet (admin@esotericsoftware.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.badlogic.gdx.ai.tests.utils;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * Utility class to launch tests.
 *
 * 
 */
public final class GdxAiTestUtils {

    private GdxAiTestUtils() {
    }

    public static void launch(ApplicationListener test) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.r = config.g = config.b = config.a = 8;
        config.width = 960;
        config.height = 600;
        new LwjglApplication(test, config);
    }
}
