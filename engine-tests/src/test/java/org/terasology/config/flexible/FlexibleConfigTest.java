/*
 * Copyright 2016 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.terasology.config.flexible;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.terasology.config.flexible.validators.RangedNumberValidator;
import org.terasology.engine.SimpleUri;

import static org.junit.Assert.*;

@RunWith(Enclosed.class)
public class FlexibleConfigTest {
    private static final SimpleUri KEY_NON_EXISTENT = new SimpleUri("engine-tests:TestSettingX");

    public static class GetTest {
        private FlexibleConfig config;

        @Before
        public void setUp() throws Exception {
            config = new FlexibleConfigImpl();
        }

        @Test
        public void testGet() throws Exception {
            SimpleUri id1 = new SimpleUri("engine-tests:TestSetting1");
            SimpleUri id2 = new SimpleUri("engine-tests:TestSetting2");

            config.add(new SettingImpl<>(id1, 50,
                    new RangedNumberValidator<>(0, 100, false, false)));

            config.add(new SettingImpl<>(id2, 25d,
                    new RangedNumberValidator<>(0d, 100d, false, false)));

            Setting<Integer> setting1 = config.get(id1);
            Setting<Double> setting2 = config.get(id2);

            assertEquals(50, setting1.getValue().intValue());
            assertEquals(25d, setting2.getValue(), 0.00001d);
        }
    }

    public static class HasTest {
        private FlexibleConfig config;

        @Before
        public void setUp() throws Exception {
            config = new FlexibleConfigImpl();
        }

        @Test
        public void testHas() throws Exception {
            SimpleUri id = new SimpleUri("engine-tests:TestSetting");

            config.add(new SettingImpl<>(id, 50,
                    new RangedNumberValidator<>(0, 100, false, false)));

            assertTrue(config.has(id));
        }

        @Test
        public void testNotHas() throws Exception {
            SimpleUri id = new SimpleUri("engine-tests:TestSetting");

            config.add(new SettingImpl<>(id, 50,
                    new RangedNumberValidator<>(0, 100, false, false)));

            assertFalse(config.has(KEY_NON_EXISTENT));
        }
    }

    public static class AddTest {
        private FlexibleConfig config;

        @Before
        public void setUp() throws Exception {
            config = new FlexibleConfigImpl();
        }

        @Test
        public void testAdd() throws Exception {
            SimpleUri id = new SimpleUri("engine-tests:TestSetting");

            assertNotNull(config.add(new SettingImpl<>(id, 50,
                    new RangedNumberValidator<>(0, 100, false, false))));
        }

        @Test
        public void testAddExisting() throws Exception {
            SimpleUri id = new SimpleUri("engine-tests:TestSetting");

            assertTrue(config.add(new SettingImpl<>(id, 50,
                    new RangedNumberValidator<>(0, 100, false, false))));

            assertFalse(config.add(new SettingImpl<>(id, 5d,
                    new RangedNumberValidator<>(1d, 100d, false, false))));
        }
    }

    public static class RemoveTest {
        private FlexibleConfig config;

        @Before
        public void setUp() throws Exception {
            config = new FlexibleConfigImpl();
        }

        @Test
        public void testRemove() throws Exception {
            SimpleUri id1 = new SimpleUri("engine-tests:TestSetting1");
            SimpleUri id2 = new SimpleUri("engine-tests:TestSetting2");

            config.add(new SettingImpl<>(id1, 50,
                    new RangedNumberValidator<>(0, 100, false, false)));

            config.add(new SettingImpl<>(id2, 25d,
                    new RangedNumberValidator<>(0d, 100d, false, false)));

            assertTrue(config.remove(id1));
            assertTrue(config.remove(id2));
        }

        @Test
        public void testNonexistentRemove() throws Exception {
            SimpleUri id = new SimpleUri("engine-tests:TestSetting");

            config.add(new SettingImpl<>(id, 50,
                    new RangedNumberValidator<>(0, 100, false, false)));

            assertFalse(config.remove(KEY_NON_EXISTENT));
        }

        @Test
        public void testSubscribedRemove() throws Exception {
            SimpleUri id = new SimpleUri("engine-tests:TestSetting");

            config.add(new SettingImpl<>(id, 50,
                    new RangedNumberValidator<>(0, 100, false, false)));

            Setting<Integer> setting = config.get(id);

            setting.subscribe(propertyChangeEvent -> {
            });

            assertFalse(config.remove(id));
        }
    }
}
