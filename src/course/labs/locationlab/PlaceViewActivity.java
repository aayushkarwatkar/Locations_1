package course.labs.locationlab;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import android.app.ListActivity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceViewActivity extends ListActivity implements LocationListener {
	private static final long FIVE_MINS = 5 * 60 * 1000;

	private static final LocationListener LocationListener = null;

	private static String TAG = "Lab-Location";

	private Location mLastLocationReading;
	private PlaceViewAdapter mAdapter;

	// default minimum time between new readings
	private long mMinTime = 5000;

	// default minimum distance between old and new readings.
	private float mMinDistance = 1000.0f;

	private LocationManager mLocationManager;

	// A fake location provider used for testing
	private MockLocationProvider mMockLocationProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // TODO - Set up the app's user interface
        // This class is a ListActivity, so it has its own ListView
        // ListView's adapter should be a PlaceViewAdapter
		mAdapter = new PlaceViewAdapter(getApplicationContext());
		getListView().setFooterDividersEnabled(true);
		TextView mfooterView = (TextView)getLayoutInflater().inflate(R.layout.footer_view, null);
		getListView().addFooterView(mfooterView);
		getListView().setAdapter(mAdapter);
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		
        // TODO - add a footerView to the ListView
        // You can use footer_view.xml to define the footer
		

		
        // TODO - When the footerView's onClick() method is called, it must issue the
        // following log call
        // log("Entered footerView.OnClickListener.onClick()");
        
       
		mfooterView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				log("Entered footerView.OnClickListener.onClick()");
				 // footerView must respond to user clicks.
		        // Must handle 3 cases:
		        // 1) The current location is new - download new Place Badge. Issue the
		        // following log call:
		        // log("Starting Place Download");
				if(mLastLocationReading==null)
				{
					log("Location data is not available");
					return;
				}
				
				
		        // 2) The current location has been seen before - issue Toast message.
		        // Issue the following log call:
		        // log("You already have this location badge");
				{
				if(mAdapter.intersects(mLastLocationReading)==true){
		        	Toast.makeText(getApplicationContext(), "Already there",Toast.LENGTH_LONG).show();
		        	log("You already have this location badge");
		        	return;
		        }
				else{
					new PlaceDownloaderTask(PlaceViewActivity.this).execute(mLastLocationReading);
					log("Starting Place Download");
				}
				}
				
				
		        // 3) There is no current location - response is up to you. The best
		        // solution is to disable the footerView until you have a location.
		        // Issue the following log call:
		        // log("Location data is not available");
				
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();

		mMockLocationProvider = new MockLocationProvider(
				LocationManager.NETWORK_PROVIDER, this);

				// TODO - Check NETWORK_PROVIDER for an existing location reading.
				// Only keep this last reading if it is fresh - less than 5 minutes old.
				if(mLastLocationReading!=null && age(mLastLocationReading)<FIVE_MINS){
				mLastLocationReading = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				}
				// TODO - register to receive location updates from NETWORK_PROVIDER
				mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mMinTime, mMinDistance, this);
					
	}

	@Override
	protected void onPause() {

		mMockLocationProvider.shutdown();

		// TODO - unregister for location updates
		mLocationManager.removeUpdates(this)
;
		
		super.onPause();
	}

	// Callback method used by PlaceDownloaderTask
	public void addNewPlace(PlaceRecord place) {

		log("Entered addNewPlace()");
		mAdapter.add(place);

	}

	@Override
	public void onLocationChanged(Location currentLocation) {

        // TODO - Handle location updates
        // Cases to consider
        // 1) If there is no last location, keep the current location.
        // 2) If the current location is older than the last location, ignore
        // the current location
        // 3) If the current location is newer than the last locations, keep the
        // current location.
		
		if(mLastLocationReading==null)
		{
			mLastLocationReading = currentLocation;
		}
		if(age(currentLocation)<age(mLastLocationReading))
		{
			mLastLocationReading = currentLocation;
		}

	}

	@Override
	public void onProviderDisabled(String provider) {
		// not implemented
	}

	@Override
	public void onProviderEnabled(String provider) {
		// not implemented
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// not implemented
	}

	private long age(Location location) {
		return System.currentTimeMillis() - location.getTime();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.print_badges:
			ArrayList<PlaceRecord> currData = mAdapter.getList();
			for (int i = 0; i < currData.size(); i++) {
				log(currData.get(i).toString());
			}
			return true;
		case R.id.delete_badges:
			mAdapter.removeAllViews();
			return true;
		case R.id.place_one:
			mMockLocationProvider.pushLocation(37.422, -122.084);
			return true;
		case R.id.place_invalid:
			mMockLocationProvider.pushLocation(0, 0);
			return true;
		case R.id.place_two:
			mMockLocationProvider.pushLocation(38.996667, -76.9275);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private static void log(String msg) {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Log.i(TAG, msg);
	}

}
