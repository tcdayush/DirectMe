package com.example.directme;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;
import java.util.Objects;

/**
     * An activity that displays a map showing the place at the device's current location.
     */
    @SuppressLint("All")
    public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

        Button buttonSearch;
        Button buttonPreference;

        Marker sourceMarker;
        Marker destinationMarker;
        private static final String TAG = MapsActivity.class.getSimpleName();
        private GoogleMap mMap;
        public CameraPosition mCameraPosition;

        // The entry points to the Places API.
        public GeoDataClient mGeoDataClient;
        private PlaceDetectionClient mPlaceDetectionClient;

        // The entry point to the Fused Location Provider.
        private FusedLocationProviderClient mFusedLocationProviderClient;

        // A default location (Sydney, Australia) and default zoom to use when location permission is
        // not granted.
        private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
        private static final int DEFAULT_ZOOM = 15;
        private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
        private boolean mLocationPermissionGranted;

        // The geographical location where the device is currently located. That is, the last-known
        // location retrieved by the Fused Location Provider.
        private Location mLastKnownLocation;

        // Keys for storing activity state.
        private static final String KEY_CAMERA_POSITION = "camera_position";
        private static final String KEY_LOCATION = "location";

        // Used for selecting the current place.
        private static final int M_MAX_ENTRIES = 5;
        private String[] mLikelyPlaceNames;
        private String[] mLikelyPlaceAddresses;
        private String[] mLikelyPlaceAttributions;
        private LatLng[] mLikelyPlaceLatLngs;

        AutocompleteSupportFragment sourceAutocompleteFragment;
        AutocompleteSupportFragment destinationAutocompleteFragment;

        LatLng sourceLatlangObj;
        LatLng destinationLatlangObj;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Retrieve location and camera position from saved instance state.
            if (savedInstanceState != null) {
                mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
                mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            }

            // Retrieve the content view that renders the map.
            setContentView(R.layout.activity_maps);

            buttonSearch = findViewById(R.id.button_search);
            buttonPreference = findViewById(R.id.button_preferences);

            // Construct a GeoDataClient.
            mGeoDataClient = Places.getGeoDataClient(this);

            // Construct a PlaceDetectionClient.
            mPlaceDetectionClient = Places.getPlaceDetectionClient(this);

            // Construct a FusedLocationProviderClient.
            mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

            // Build the map.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            com.google.android.libraries.places.api.Places.initialize(getApplicationContext(), "AIzaSyBPFLxUS38OVBy6Na5fzroAMBo-Ka8-CKs");

            sourceAutocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.source_autocomplete_fragment);
            Objects.requireNonNull(sourceAutocompleteFragment).setHint("Source");

            destinationAutocompleteFragment = (AutocompleteSupportFragment)
                    getSupportFragmentManager().findFragmentById(R.id.destination_autocomplete_fragment);
            Objects.requireNonNull(destinationAutocompleteFragment).setHint("Destination");

            // Specify the types of place data to return.
            sourceAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                    Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS));
            destinationAutocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,
                    Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS));

            // Set up a PlaceSelectionListener to handle the response.
            sourceAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {

                    if(sourceMarker != null) {
                        sourceMarker.remove();
                    }

                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                    sourceLatlangObj = place.getLatLng();
                    assert sourceLatlangObj != null;
                    Log.v("latitude:", "" + sourceLatlangObj.latitude);
                    Log.v("longitude:", "" + sourceLatlangObj.longitude);

                    sourceMarker = mMap.addMarker(new MarkerOptions().position(sourceLatlangObj)
                            .title("Source: " + place.getName()));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(sourceLatlangObj));
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.i(TAG, "An error occurred: " + status);
                }
            });

            destinationAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {

                    if(destinationMarker != null) {
                        destinationMarker.remove();
                    }

                    Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
                    destinationLatlangObj = place.getLatLng();
                    assert destinationLatlangObj != null;
                    Log.v("latitude:", "" + destinationLatlangObj.latitude);
                    Log.v("longitude:", "" + destinationLatlangObj.longitude);

                    destinationMarker = mMap.addMarker(new MarkerOptions().position(destinationLatlangObj)
                            .title("Destination: " + place.getName()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(destinationLatlangObj));
                }

                @Override
                public void onError(@NonNull Status status) {
                    Log.i(TAG, "An error occurred: " + status);
                }
            });

            /*buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //TODO: Send the Preferencs and Location details to Server
                    sourceAutocompleteFragment.getText();

                    inputDestinationString = inputDestination.getText().toString().trim();

                    if(inputSourceString.isEmpty()|| inputDestinationString.isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"Please Enter Source and Destination Correctly",
                                Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Intent intent = new Intent(MapsActivity.this, Routes.class);
                        startActivity(intent);
                    }

                }
            });*/

            buttonPreference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MapsActivity.this, Preferences.class);
                    startActivity(intent);
                }
            });

        }



        /**
         * Saves the state of the map when the activity is paused.
         */
        @Override
        protected void onSaveInstanceState(Bundle outState) {
            if (mMap != null) {
                outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
                outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
                super.onSaveInstanceState(outState);
            }
        }

        /**
         * Sets up the options menu.
         * @param menu The options menu.
         * @return Boolean.
         */
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.current_place_menu, menu);
            return true;
        }

        /**
         * Handles a click on the menu option to get a place.
         * @param item The menu item to handle.
         * @return Boolean.
         */
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.option_get_place) {
                showCurrentPlace();
            }
            return true;
        }

        /**
         * Manipulates the map when it's available.
         * This callback is triggered when the map is ready to be used.
         */
        @Override
        public void onMapReady(GoogleMap map) {
            mMap = map;



            // Use a custom info window adapter to handle multiple lines of text in the
            // info window contents.
            mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

                @Override
                // Return null here, so that getInfoContents() is called next.
                public View getInfoWindow(Marker arg0) {
                    return null;
                }

                @Override
                public View getInfoContents(Marker marker) {
                    // Inflate the layouts for the info window, title and snippet.
                    View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                            (FrameLayout) findViewById(R.id.map), false);

                    TextView title = (infoWindow.findViewById(R.id.title));
                    title.setText(marker.getTitle());

                    TextView snippet = (infoWindow.findViewById(R.id.snippet));
                    snippet.setText(marker.getSnippet());

                    return infoWindow;
                }
            });

            // Prompt the user for permission.
            getLocationPermission();

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
        }

        /**
         * Gets the current location of the device, and positions the map's camera.
         */
        private void getDeviceLocation() {
            /*
             * Get the best and most recent location of the device, which may be null in rare
             * cases when a location is not available.
             */
            try {
                if (mLocationPermissionGranted) {
                    Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                // Set the map's camera position to the current location of the device.
                                mLastKnownLocation = task.getResult();
                                if (mLastKnownLocation != null) {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                            new LatLng(mLastKnownLocation.getLatitude(),
                                                    mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                }
                            } else {
                                Log.d(TAG, "Current location is null. Using defaults.");
                                Log.e(TAG, "Exception: %s", task.getException());
                                mMap.moveCamera(CameraUpdateFactory
                                        .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                            }
                        }
                    });
                }
            } catch (SecurityException e)  {
                Log.e("Exception: %s", e.getMessage());
            }
        }


        /**
         * Prompts the user for permission to use the device location.
         */
        private void getLocationPermission() {
            /*
             * Request location permission, so that we can get the location of the
             * device. The result of the permission request is handled by a callback,
             * onRequestPermissionsResult.
             */
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        }

        /**
         * Handles the result of the request for location permissions.
         */
        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String permissions[],
                                               @NonNull int[] grantResults) {
            mLocationPermissionGranted = false;
            switch (requestCode) {
                case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mLocationPermissionGranted = true;
                    }
                }
            }
            updateLocationUI();
        }

        /**
         * Prompts the user to select the current place from a list of likely places, and shows the
         * current place on the map - provided the user has granted location permission.
         */
        private void showCurrentPlace() {
            if (mMap == null) {
                return;
            }

            if (mLocationPermissionGranted) {
                // Get the likely places - that is, the businesses and other points of interest that
                // are the best match for the device's current location.
                @SuppressWarnings("MissingPermission") final
                Task<PlaceLikelihoodBufferResponse> placeResult =
                        mPlaceDetectionClient.getCurrentPlace(null);
                placeResult.addOnCompleteListener
                        (new OnCompleteListener<PlaceLikelihoodBufferResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlaceLikelihoodBufferResponse> task) {
                                if (task.isSuccessful() && task.getResult() != null) {
                                    PlaceLikelihoodBufferResponse likelyPlaces = task.getResult();

                                    // Set the count, handling cases where less than 5 entries are returned.
                                    int count;
                                    if (likelyPlaces.getCount() < M_MAX_ENTRIES) {
                                        count = likelyPlaces.getCount();
                                    } else {
                                        count = M_MAX_ENTRIES;
                                    }

                                    int i = 0;
                                    mLikelyPlaceNames = new String[count];
                                    mLikelyPlaceAddresses = new String[count];
                                    mLikelyPlaceAttributions = new String[count];
                                    mLikelyPlaceLatLngs = new LatLng[count];

                                    for (PlaceLikelihood placeLikelihood : likelyPlaces) {
                                        // Build a list of likely places to show the user.
                                        mLikelyPlaceNames[i] = (String) placeLikelihood.getPlace().getName();
                                        mLikelyPlaceAddresses[i] = (String) placeLikelihood.getPlace()
                                                .getAddress();
                                        mLikelyPlaceAttributions[i] = (String) placeLikelihood.getPlace()
                                                .getAttributions();
                                        mLikelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();

                                        i++;
                                        if (i > (count - 1)) {
                                            break;
                                        }
                                    }

                                    // Release the place likelihood buffer, to avoid memory leaks.
                                    likelyPlaces.release();

                                    // Show a dialog offering the user the list of likely places, and add a
                                    // marker at the selected place.
                                    openPlacesDialog();

                                } else {
                                    Log.e(TAG, "Exception: %s", task.getException());
                                }
                            }
                        });
            } else {
                // The user has not granted permission.
                Log.i(TAG, "The user did not grant location permission.");

                // Add a default marker, because the user hasn't selected a place.
                mMap.addMarker(new MarkerOptions()
                        .title(getString(R.string.default_info_title))
                        .position(mDefaultLocation)
                        .snippet(getString(R.string.default_info_snippet)));

                // Prompt the user for permission.
                getLocationPermission();
            }
        }

        /**
         * Displays a form allowing the user to select a place from a list of likely places.
         */
        private void openPlacesDialog() {
            // Ask the user to choose the place where they are now.
            DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // The "which" argument contains the position of the selected item.
                    LatLng markerLatLng = mLikelyPlaceLatLngs[which];
                    String markerSnippet = mLikelyPlaceAddresses[which];
                    if (mLikelyPlaceAttributions[which] != null) {
                        markerSnippet = markerSnippet + "\n" + mLikelyPlaceAttributions[which];
                    }

                    // Add a marker for the selected place, with an info window
                    // showing information about that place.
                    mMap.addMarker(new MarkerOptions()
                            .title(mLikelyPlaceNames[which])
                            .position(markerLatLng)
                            .snippet(markerSnippet));

                    // Position the map's camera at the location of the marker.
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerLatLng,
                            DEFAULT_ZOOM));
                }
            };

            // Display the dialog.
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.pick_place)
                    .setItems(mLikelyPlaceNames, listener)
                    .show();
            dialog.show();
        }

        /**
         * Updates the map's UI settings based on whether the user has granted location permission.
         */
        private void updateLocationUI() {
            if (mMap == null) {
                return;
            }
            try {
                if (mLocationPermissionGranted) {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                } else {
                    mMap.setMyLocationEnabled(false);
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    mLastKnownLocation = null;
                    getLocationPermission();
                }
            } catch (SecurityException e)  {
                Log.e("Exception: %s", e.getMessage());
            }
        }
    }
