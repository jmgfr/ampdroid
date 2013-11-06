package com.racoon.ampdroid;

import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

	private String[] mNavItems;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private Controller controller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		controller = Controller.getInstance();
		mNavItems = getResources().getStringArray(R.array.menu_array);
		mDrawerTitle = getResources().getString(R.string.app_name);
		mTitle = controller.getFragmentsNames()[0];

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setTitle(mTitle);

		/** Media Control **/
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		if (controller.getServerConfig(this) != null
				&& controller.getServer().isConnected(controller.isOnline(getApplicationContext()))) {

			Log.d("bugs", controller.getServer().getAmpacheConnection().getAuth());
			// controller.saveServer(getApplicationContext());
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[0]));
			tx.commit();
			Context context = getApplicationContext();
			CharSequence text = "Verbindung zum Server hergestellt";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else if (controller.getServerConfig(this) != null
				&& !controller.getServer().isConnected(controller.isOnline(getApplicationContext()))) {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[5]));
			tx.commit();
			mTitle = controller.getFragmentsNames()[6];
			getActionBar().setTitle(mTitle);
			Context context = getApplicationContext();
			CharSequence text = "Verbindung zum Server ist nicht möglich";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[5]));
			tx.commit();
			mTitle = controller.getFragmentsNames()[5];
			getActionBar().setTitle(mTitle);
			Context context = getApplicationContext();
			CharSequence text = "Einstellungen sind noch nicht gesetzt";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		}

		// just styling option add shadow the right edge of the drawer
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely open state. */
			@Override
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mNavItems));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int pos, long id) {
				mDrawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
					@Override
					public void onDrawerClosed(View drawerView) {
						super.onDrawerClosed(drawerView);
						FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
						tx.replace(R.id.content_frame,
								Fragment.instantiate(MainActivity.this, controller.getFragments()[pos]));
						tx.commit();
						getActionBar().setTitle(controller.getFragmentsNames()[pos]);
						invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
					}

					/** Called when a drawer has settled in a completely open state. */
					@Override
					public void onDrawerOpened(View drawerView) {
						getActionBar().setTitle(mDrawerTitle);
						invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
					}
				});
				mDrawerLayout.closeDrawer(mDrawerList);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content view
		// boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		// menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...
		if (item.toString().equals(getResources().getString(R.string.action_settings))) {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[5]));
			tx.commit();
			getActionBar().setTitle(R.string.action_settings);
		}
		return super.onOptionsItemSelected(item);
	}

	public void saveSettings(View view) {
		String server = ((EditText) findViewById(R.id.input_server)).getText().toString();
		String user = ((EditText) findViewById(R.id.input_user)).getText().toString();
		String password = ((EditText) findViewById(R.id.input_password)).getText().toString();
		if (controller.getServer() != null && !controller.getServer().getPassword().equals(password)) {
			password = controller.generateShaHash(password);
		}
		controller.saveSettings(password, user, server);
		if (!controller.saveServer(getApplicationContext())) {
			Context context = getApplicationContext();
			CharSequence text = "Einstellungen konnten nicht gespeichert werden";
			int duration = Toast.LENGTH_LONG;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
		} else {
			Context context = getApplicationContext();
			CharSequence text = "Einstellungen wurden gespeichert";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();
			if (!this.controller.getServer().isConnected(controller.isOnline(getApplicationContext()))) {
				text = "Verbindung konnte nicht hergestellt werden";
				toast = Toast.makeText(context, text, duration);
				toast.show();
			} else {
				text = "Verbindung wurde hergestellt";
				toast = Toast.makeText(context, text, duration);
				toast.show();
				FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
				tx.replace(R.id.content_frame, Fragment.instantiate(MainActivity.this, controller.getFragments()[0]));
				tx.commit();
				getActionBar().setTitle(controller.getFragmentsNames()[0]);
			}
		}
	}

	public void pause(View view) {
		controller.getMediaPlayer().pause();
	}

	public void play(View view) {
		controller.getMediaPlayer().start();
	}

	public void next(View view) {

	}

	public void previous(View view) {

	}
}