package ru.aviasales.template.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import ru.aviasales.core.AviasalesSDK;
import ru.aviasales.core.identification.IdentificationData;
import ru.aviasales.core.search_airports.object.PlaceData;
import ru.aviasales.template.R;
import ru.aviasales.template.ui.listener.AviasalesImpl;
import ru.aviasales.template.ui.model.SearchFormData;

public class AviasalesFragment extends Fragment implements AviasalesImpl {

	public final static String TAG = "aviasales_fragment";
	private final static String TAG_CHILD = "aviasales_child_fragment";

	private final static int CACHE_SIZE = 20 * 1024 * 1024;
	private final static int CACHE_FILE_COUNT = 100;
	private final static int MEMORY_CACHE_SIZE = 5 * 1024 * 1024;

	private FragmentManager fragmentManager;

	private SearchFormData searchFormData;

	private View rootView;

	public static Fragment newInstance() {
		return new AviasalesFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		searchFormData = new SearchFormData(getActivity().getApplicationContext());
		AviasalesSDK.getInstance().init(getActivity().getApplicationContext(), new IdentificationData("74590", "complex_master_api_token_here"));
		initImageLoader(getActivity().getApplicationContext());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.aviasales_fragment_layout, null);

		return rootView;
	}

	public FragmentManager getAviasalesFragmentManager() {
		return fragmentManager;
	}

	public void startFragment(BaseFragment fragment, boolean shouldAddToBackStack) {

		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		fragmentTransaction.replace(R.id.fragment_child_place, fragment, fragment.getClass().getSimpleName());
		if (shouldAddToBackStack) {
			fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
		}
		fragmentTransaction.commit();

	}

	public void popFragmentFromBackStack() {
		onBackPressed();
	}


	private void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.diskCacheSize(CACHE_SIZE)
				.diskCacheFileCount(CACHE_FILE_COUNT)
				.memoryCacheSize(MEMORY_CACHE_SIZE)
				.build();
		ImageLoader.getInstance().init(config);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		fragmentManager = this.getChildFragmentManager();

		Fragment fragment;
		if ((fragmentManager.findFragmentByTag(TAG_CHILD)) == null) {
			fragment = SearchFormFragment.newInstance();
			fragmentManager.beginTransaction().replace(R.id.fragment_child_place, fragment, TAG_CHILD).commit();
		}

	}

	public boolean onBackPressed() {
		if (fragmentManager.getBackStackEntryCount() > 0) {
			fragmentManager.popBackStack();
			return true;
		} else {
			return false;
		}
	}

	public void popBackStackInclusive(String tag) {
		fragmentManager.popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void onStop() {
		searchFormData.saveState();
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if (rootView != null) {
			ViewGroup parentViewGroup = (ViewGroup) rootView.getParent();
			if (parentViewGroup != null) {
				parentViewGroup.removeAllViews();
			}
		}
	}

	@Override
	public void onAirportSelected(PlaceData placeData, int typeOfDate, int segment, boolean isComplex) {
		if (isComplex) {
			if (typeOfDate == SelectAirportFragment.TYPE_ORIGIN) {
				getSearchFormData().getComplexSearchSegments().get(segment).setOrigin(placeData);
			} else {
				getSearchFormData().getComplexSearchSegments().get(segment).setDestination(placeData);
			}
		} else {
			if (typeOfDate == SelectAirportFragment.TYPE_ORIGIN) {
				getSearchFormData().getSimpleSearchParams().setOrigin(placeData);
			} else {
				getSearchFormData().getSimpleSearchParams().setDestination(placeData);
			}
		}
	}

	@Override
	public SearchFormData getSearchFormData() {
		return searchFormData;
	}

	@Override
	public void saveState() {
		searchFormData.saveState();
	}
}
