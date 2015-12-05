package pl.droidcon.dagger2hacks;

import com.google.auto.value.AutoAnnotation;
import com.google.common.collect.Sets;

import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Qualifier;

import dagger.MapKey;
import dagger.Module;
import dagger.Provides;

import static com.google.common.truth.Truth.assertThat;

public class DaggerHacks {

    @AutoAnnotation
    static CustomKey createKey(String value, int another) {
        return new AutoAnnotation_DaggerHacks$CustomKeyCreator_createCustomKey(value, another);
    }

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Custom {

    }

    @Inject
    Set<String> test;

    @Inject
    @Custom
    Set<String> customStrings;

    @Inject
    Map<CustomKey, Long> myMap;

    @Before
    public void setUp() throws Exception {
        DaggerDaggerHacks_Component
                .builder()
                .sampleModule(new SampleModule())
                .build()
                .inject(this);
    }

    @Test
    public void shouldWork() {
        assertThat(test).containsExactly("ABC", "DEF", "one", "two", "three");
    }

    @Test
    public void mapKeysShouldWorkIHope() throws Exception {
        assertThat(myMap).containsEntry(createKey("hello", 5), 5L);
    }

    @Test
    public void shouldWorkWithAQualifier() {
        assertThat(customStrings).containsExactly("tip1", "tip2");
    }

    @dagger.Component(modules = SampleModule.class)
    public interface Component {
        void inject(DaggerHacks inject);
    }

    @MapKey(unwrapValue = false)
    @interface CustomKey {
        String value();
        int another();
    }

    @Module
    public class SampleModule {
        @Provides(type = Provides.Type.SET)
        public String provideString1() {
            return "ABC";
        }

        @Provides(type = Provides.Type.SET)
        public String provideString2() {
            return "DEF";
        }

        @Custom
        @Provides (type = Provides.Type.SET)
        public String provideCustom1() {
            return "tip1";
        }

        @Custom
        @Provides(type = Provides.Type.SET)
        public String provideCustom2() {
            return "tip2";
        }

        @Provides(type = Provides.Type.SET_VALUES)
        public Set<String> anotherStrings() {
            return Sets.newHashSet("one", "two", "three");
        }

        @Provides(type = Provides.Type.MAP)
        @CustomKey(value = "hello", another = 5)
        Long provideHello() {
            return 5L;
        }
    }
}