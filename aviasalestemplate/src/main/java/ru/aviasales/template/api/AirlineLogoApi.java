package ru.aviasales.template.api;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.template.api.params.AirlineLogoParams;
import ru.aviasales.template.utils.Defined;


public class AirlineLogoApi {

	private DisplayImageOptions displayOptions = new DisplayImageOptions.Builder()
			.cacheInMemory(true)
			.cacheOnDisk(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.resetViewBeforeLoading(true)
			.build();

	public void getAirlineLogo(final AirlineLogoParams params) {
		String searchHost = AviasalesSDK.getInstance().getSdkHost();

		if (searchHost == null) {
			throw new NullPointerException("AviasalesSDK searchHost is null. Should be initialized before usage");
		}

		ImageLoader.getInstance().displayImage(
				getUrl(params.getIata(), params.getWidth(), params.getHeight(), searchHost),
				params.getImage(),
				displayOptions,
				params.getImageLoadingListener());
	}

	public static String getUrl(String iata, int logoWidth, int logoHeight, String searchUrl) {
		return Defined.getAirlineLogoTemplateUrl()
				.replace("{Width}", String.valueOf(logoWidth))
				.replace("{Height}", String.valueOf(logoHeight))
				.replace("{SearchUrl}", searchUrl)
				.replace("{IATA}", iata);
	}
}
