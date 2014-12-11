package com.github.leichtundkross.configservice;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import com.github.leichtundkross.configservice.ConfigurationEntity;
import com.github.leichtundkross.configservice.ConfigurationEntityBuilder;
import com.github.leichtundkross.configservice.ConfigurationEntityValidator;
import com.github.leichtundkross.configservice.ConfigurationEntryDAO;
import com.github.leichtundkross.configservice.ConfigurationService;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceTest {

	private static final String PROP_KEY = "myKey";

	@InjectMocks
	private ConfigurationService configService;

	@Mock
	private ConfigurationEntryDAO dao;

	@Mock
	private ConfigurationEntityValidator validator;

	@Before
	@SuppressWarnings("unchecked")
	public void mockValidator() {
		Mockito.when(validator.validate(Matchers.any(ConfigurationEntity.class), Matchers.anyString())).then(new Answer<String>() {

			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
				return ((ConfigurationEntity<String>) invocation.getArguments()[0]).getEntries().get(0).getValue();
			}
		});
	}

	@Test
	@SuppressWarnings("unchecked")
	public void readProperty() {
		Mockito.when(dao.load(PROP_KEY, String.class)).thenReturn(ConfigurationEntityBuilder.<String> createConfig().value("helloProperty").getConfigEntry());

		assertEquals("helloProperty", configService.readProperty(PROP_KEY, String.class));

		Mockito.verify(dao, Mockito.never()).save(Matchers.anyString(), Matchers.any(ConfigurationEntity.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void readProperty_SetDefault() {
		Mockito.when(dao.load(PROP_KEY, String.class)).thenReturn(ConfigurationEntityBuilder.<String> createConfig().value(null).getConfigEntry());
		Mockito.doReturn(null).when(validator).validate(Matchers.any(ConfigurationEntity.class), Matchers.eq(PROP_KEY));

		assertEquals("defaultValue", configService.readProperty(PROP_KEY, String.class, "defaultValue"));

		Mockito.verify(dao).save(Matchers.eq(PROP_KEY), matchConfigEntity("defaultValue"));
	}

	@Test
	public void writeProperty() {
		configService.writeProperty(PROP_KEY, "myProperty");

		InOrder callOrder = Mockito.inOrder(validator, dao);
		callOrder.verify(validator).validate(matchConfigEntity("myProperty"), Matchers.eq(PROP_KEY));
		callOrder.verify(dao).save(Matchers.eq(PROP_KEY), matchConfigEntity("myProperty"));
	}

	@Test
	public void testWriteProperty_ConfigBuilder() {
		Date validFrom = new Date();

		configService.writeProperty(PROP_KEY, ConfigurationEntityBuilder.<String> createConfig().value("myProperty").validFrom(validFrom));

		InOrder callOrder = Mockito.inOrder(validator, dao);
		callOrder.verify(validator).validate(matchConfigEntity("myProperty", validFrom), Matchers.eq(PROP_KEY));
		callOrder.verify(dao).save(Matchers.eq(PROP_KEY), matchConfigEntity("myProperty", validFrom));
	}

	private static <T> ConfigurationEntity<T> matchConfigEntity(T configValue) {
		return matchConfigEntity(configValue, null);
	}

	@SuppressWarnings("unchecked")
	private static <T> ConfigurationEntity<T> matchConfigEntity(T configValue, Date validFrom) {
		final ConfigurationEntity<T> expectedEntity = ConfigurationEntityBuilder.<T> createConfig().value(configValue).validFrom(validFrom).getConfigEntry();
		return Matchers.argThat(new BaseMatcher<ConfigurationEntity<T>>() {

			public boolean matches(Object item) {
				ConfigurationEntity<T> entity = (ConfigurationEntity<T>) item;

				assertEquals(expectedEntity.getEntries().size(), entity.getEntries().size());
				for (int i = 0; i < expectedEntity.getEntries().size(); i++) {
					assertEquals(expectedEntity.getEntries().get(i).getValue(), entity.getEntries().get(i).getValue());
					assertEquals(expectedEntity.getEntries().get(i).getValidFrom(), entity.getEntries().get(i).getValidFrom());
					assertEquals(expectedEntity.getEntries().get(i).getValidTo(), entity.getEntries().get(i).getValidTo());
				}

				return true;
			}

			public void describeTo(Description arg0) {
				// do nothing
			}
		});
	}
}