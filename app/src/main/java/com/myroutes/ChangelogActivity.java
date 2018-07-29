package com.myroutes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import com.myroutes.Changelog.ChangeLogUpdates;
import com.myroutes.Changelog.ChangelogAdapter;

import java.util.ArrayList;

public class ChangelogActivity extends AppCompatActivity {
  static ArrayList<ChangeLogUpdates> list = new ArrayList<ChangeLogUpdates>() {
    {
      add(new ChangeLogUpdates("14-07-2017", "2.1.9",
          "- Reversed to donation business model, no ads again!<br>" +
              "- Removed confirm password<br>" +
              "- Small tweaks"));
      add(new ChangeLogUpdates("14-07-2017", "2.1.0",
          "- Routes uploaded will show Height Profile<br>" +
              "- Removed confirm password<br>" +
              "- Small tweaks"));
      add(new ChangeLogUpdates("05-07-2017", "2.0.9",
          "- Downloading routes improved<br>" +
              "- Save route directly when opened from site<br>"));
      add(new ChangeLogUpdates("12-06-2017", "2.0.9",
          "- Privacy Policy and Terms of service are clickable<br>" +
              "- Save route directly when opened from site<br>"));
      add(new ChangeLogUpdates("26-05-2017", "2.0.8",
          "Downloading routes optimized for events"));
      add(new ChangeLogUpdates("25-05-2017", "2.0.7",
          "Hide start & stop markers initially, tablet resource fixes "));
      add(new ChangeLogUpdates("25-05-2017", "2.0.6",
          "Resolved some crash reports<br>"));
      add(new ChangeLogUpdates("25-05-2017", "2.0.5",
          "- LoginFix<br>"));
      add(new ChangeLogUpdates("24-05-2017", "2.0.4",
          "- Hotfix of previous release"));
      add(new ChangeLogUpdates("24-05-2017", "2.0.3",
          "- Importing gpx from other apps (Fixes)<br>" +
              "- Start viewmode while file has been imported<br>" +
              "- Start/End-marker show consistently<br>" +
              "- Updating marker zoomlevels more efficiently<br>"));
      add(new ChangeLogUpdates("22-05-2017", "2.0.2",
          "- Distance Fixes<br>" +
              "- Clear All Fix<br>" +
              "- Import GPX fix<br>"));
      add(new ChangeLogUpdates("20-05-2017", "2.0.1",
          "- Small Fixes"));
      add(new ChangeLogUpdates("16-05-2017", "2.0.0",
          "- Added account functionality<br>" +
              "- Sign up/Login/Forgot password<br>" +
              "- Google Plus Authentication<br>" +
              "- Upload routes with your userId<br>" +
              "- Use SD card for offline maps<br>" +
              "- Implemented multi threading"));
      add(new ChangeLogUpdates("14-05-2017", "1.6.7",
          "- Switching between imported route and not important route fails<br>" +
              "- Request Permission Fixed"));
      add(new ChangeLogUpdates("12-05-2017", "1.6.6",
          "- Resized icons for tablets<br>" +
              "- More optimizations<br>" +
              "- Please keep sending crash reports>"));
      add(new ChangeLogUpdates("11-05-2017", "1.6.5",
          "- Resolved frame drop when zooming<br>" +
              "- Offline Maps crash fixed"));
      add(new ChangeLogUpdates("05-05-2017", "1.6.4",
          "- Icons added to route creation<br>" +
              "- Add IconBox (long press on map to show)<br>" +
              "- Remove icons by clicking already placed icons<br>" +
              "- Resolved bug where zooming out showed distance marker every 1000 units<br>"));
      add(new ChangeLogUpdates("05-05-2017", "1.6.3",
          "- Automatically update distance markers based on zoom level<br>" +
              "- Implemented busy state for drawing new lines<br>" +
              "- Recalculate bounds of route when orientation changes in import mode"));
      add(new ChangeLogUpdates("04-05-2017", "1.6.2",
          "Distance markers use imperial settings now too."));
      add(new ChangeLogUpdates("03-05-2017", "1.6.1",
          "Distance Markers added. Check the settings."));
      add(new ChangeLogUpdates("02-05-2017", "1.6.0",
          "Translation fixes"));
      add(new ChangeLogUpdates("02-05-2017", "1.5.9",
          "- Dutch translation improved, Swedish and Japanese added<br>" +
              "- Many bugfixes"));
      add(new ChangeLogUpdates("27-04-2017", "1.5.8",
          "- Fit Bounds to route<br>" +
              "- Paypal & bitcoin donations are not allowed by Playstore TOS :("));
      add(new ChangeLogUpdates("26-04-2017", "1.5.7",
          "- Create Route Silhouette with image<br>" +
              "- Few bug fixes"));
      add(new ChangeLogUpdates("25-04-2017", "1.5.6",
          "Added donation options: Google, Bitcoin and Paypal"));
      add(new ChangeLogUpdates("25-04-2017", "1.5.5",
          "- In view mode limit zoom out level to 13<br>" +
              "- Setting up billing permission for upcoming donation page"));
      add(new ChangeLogUpdates("20-04-2017", "1.5.4",
          "- Offline route fixed<br>" +
              "- Menu bug fixed"));
      add(new ChangeLogUpdates("19-04-2017", "1.5.3",
          "When planning routes from a different location, only remove 1 line segment max"));
      add(new ChangeLogUpdates("19-04-2017", "1.5.2",
          "- For non GPS users, app will now save your previously searched location as your starting point<br>" +
              "- More crash report fixes :)<br>" +
              "- Also check out <a href='https://myroutes.io'>MyRoutes.io</a>. You can embed routes on your own site!"));
      add(new ChangeLogUpdates("18-04-2017", "1.5.1",
          "- Save Route bug fixed, my apologies. Thanks for the crash reports"));
      add(new ChangeLogUpdates("17-04-2017", "1.5.0",
          "- Uploading routes to server used the wrong distance value, now it uses metric by default and will be converted client side<br>" +
              "- Searching for a place will persist focus on this place even if the undo button is clicked<br>" +
              "- Overwriting existing routes bug fixes<br>" +
              "- View mode shows active route name"));
      add(new ChangeLogUpdates("15-04-2017", "1.4.9",
          "- Import GPX fix"));
      add(new ChangeLogUpdates("15-04-2017", "1.4.8",
          "- Big cleanup (routes before 1.4.0 will not work)<br>" +
              "- Show endmarker in VIEW mode<br>" +
              "- View mode: keep screen on & follow user position<br>" +
              "- View mode: now in fullscreen<br>" +
              "- Action menu: addition of icons"));
      add(new ChangeLogUpdates("13-04-2017", "1.4.7",
          "- Bugfix Google Places API center last marker"));
      add(new ChangeLogUpdates("13-04-2017", "1.4.6",
          "- Show start marker<br>" +
              "- Go back to start point"));
      add(new ChangeLogUpdates("11-04-2017", "1.4.5",
          "- Uploading routes now less redundant<br>" +
              "- Toggle VIEW MODE"));
      add(new ChangeLogUpdates("04-04-2017", "1.4.4",
          "- Startmarker and endmarker same as site<br>" +
              "- Updated menu design"));
      add(new ChangeLogUpdates("03-04-2017", "1.4.3",
          "- Bug fixes"));
      add(new ChangeLogUpdates("01-04-2017", "1.4.2",
          "- Bug fixes"));
      add(new ChangeLogUpdates("31-03-2017", "1.4.1",
          "- Attribution for maps<br>" +
              "- Redesign of core code to improve efficiency"));
      add(new ChangeLogUpdates("27-03-2017", "1.4.0",
          "- Path will now be drawn all the way instead of a limited distance<br>" +
              "- Fixed a few crashes<br>" +
              "- Tablet design fixes"));
      add(new ChangeLogUpdates("24-03-2017", "1.3.9",
          "- Menu Addition: Write review"));
      add(new ChangeLogUpdates("23-03-2017", "1.3.8",
          "- Small changes to GPX format"));
      add(new ChangeLogUpdates("22-03-2017", "1.3.7",
          "- GPX format suitable for Garmin connect"));
      add(new ChangeLogUpdates("21-03-2017", "1.3.6",
          "- Set location on startup correctly<br>" +
              "- Fix fab menu location"));
      add(new ChangeLogUpdates("21-03-2017", "1.3.5",
          "- UI update<br>" +
              "- Removed splashscreen & about page (refer to website from now)"));
      add(new ChangeLogUpdates("19-03-2017", "1.3.4",
          "- Optimized Snap To Road: route can be 3 times longer<br>" +
              "- Change line color<br>" +
              "- Persistent travel mode"));
      add(new ChangeLogUpdates("12-03-2017", "1.3.3",
          "MyRoutes.io improved integration"));
      add(new ChangeLogUpdates("10-03-2017", "1.3.2",
          "UI update"));
      add(new ChangeLogUpdates("09-03-2017", "1.3.1",
          "Russian, Portuguese and Italian added"));
      add(new ChangeLogUpdates("07-03-2017", "1.3.0",
          "- Do not require GPS permission to plan routes<br>" +
              "- Change marker position when going to a different location through Google Places API"));
      add(new ChangeLogUpdates("05-03-2017", "1.2.9",
          "Fix user submitted crash reports"));
      add(new ChangeLogUpdates("05-03-2017", "1.2.8",
          "- Fix cycling mode"));
      add(new ChangeLogUpdates("03-03-2017", "1.2.7",
          "Create Route Silhouette"));
      add(new ChangeLogUpdates("28-02-2017", "1.2.6",
          "Route upload fixed"));
      add(new ChangeLogUpdates("28-02-2017", "1.2.5",
          "French translation by Niels de Winter"));
      add(new ChangeLogUpdates("27-02-2017", "1.2.4",
          "German translation by Arjen van der Schaaf"));
      add(new ChangeLogUpdates("25-02-2017", "1.2.3",
          "Added Google Places API"));
      add(new ChangeLogUpdates("25-02-2017", "1.2.2",
          "Revert test values in release"));
      add(new ChangeLogUpdates("25-02-2017", "1.2.1",
          "- Set zoom levels in options<br>" +
              "- More consistent design<br>" +
              "- Fixed user submitted error"));
      add(new ChangeLogUpdates("22-02-2017", "1.2.0",
          "- HTTPS hotfix<br>" +
              "- Email address updated"));
      add(new ChangeLogUpdates("19-02-2017", "1.1.9",
          "Small namechanges"));
      add(new ChangeLogUpdates("18-02-2017", "1.1.8",
          "Offline Maps Integration"));
      add(new ChangeLogUpdates("16-02-2017", "1.1.7",
          "- Upload and retrieve routes to MyRoutes.io<br>" +
              "- Decide whether you want to make a route publicly available in the register or not<br>" +
              "- Few stability updates and crash preventions<br>" +
              "- Dutch translation (Fullhdpixel)<br>" +
              "- Spanish translation (Alvaro Korstanje Vallejo)"));
      add(new ChangeLogUpdates("06-02-2017", "1.1.6",
          "Small bugfixes"));
      add(new ChangeLogUpdates("06-02-2017", "1.1.5",
          "- 11 New Maptypes for MapBox<br>" +
              "- Change Quality for MapBox tiles<br>" +
              "- Cleanup menu (New Icon)<br>" +
              "- User validation on save route<br>" +
              "- Trim routenames<br>" +
              "- Changed start/end icon for import mode<br>" +
              "- About page"));
      add(new ChangeLogUpdates("18-01-2017", "1.1.4",
          "- New options menu<br>" +
              "- New option: Semi Transparent Toolbar"));
      add(new ChangeLogUpdates("14-01-2017", "1.1.3",
          "- Kilometer changed to metric, miles changed to imperial<br>" +
              "- UI color patch<br>" +
              "- logo in drawer 700 * 366<br>" +
              "- Open files from any other app"));
      add(new ChangeLogUpdates("11-01-2017", "1.1.2",
          "- Dont pass through intent, add to savedinstancestate<br>" +
              "- Performance improvement"));
      add(new ChangeLogUpdates("09-01-2017", "1.1.1",
          "Stability update:<br>" +
              "- Delete route bug fixed (problem was naming)<br>" +
              "- StartFromMarker persistent"));
      add(new ChangeLogUpdates("09-01-2017", "1.1.0",
          "- Bugfix: check internet connection before fetching data<br>" +
              "- Updated dialogs to material design <br>" +
              "- Improved overall stability of application<br>" +
              "- Please keep sending bug reports!"));
      add(new ChangeLogUpdates("05-01-2017", "1.0.9",
          "- Added two more maptiles: Lyrk & MapBox<br>" +
              "- Removed base layer of google maps (no double labels)<br>" +
              "- No Routes Saved bug fixed<br>" +
              "- Theme bar color<br>" +
              "- Delete button state fixed<br>" +
              "- Added Changelog history, this is some recursive shit :)"));
      add(new ChangeLogUpdates("04-01-2017", "1.0.8",
          "Two bug fixes: changing orientation on startup fixed and dont fire onwindowchanged twice"));
      add(new ChangeLogUpdates("03-01-2017", "1.0.7",
          ".xml files from the file browser can be opened directly with MyRoutes"));
      add(new ChangeLogUpdates("03-01-2017", "1.0.6",
          "Many small bug fixes and small UI updates. Moved map types to option menu."));
      add(new ChangeLogUpdates("02-01-2017", "1.0.5",
          "New functionality:<br>" +
              "- Import GPX files<br>" +
              "- Save and share GPX files!"));
      add(new ChangeLogUpdates("31-12-2016", "1.0.3",
          "Bug fixes & UI update:<br>" +
              "- Changing screen orientation bug fixed<br>" +
              "- Changing snap to road in menu will change button circular progress<br>" +
              "- Added splash screen animation intro"));
      add(new ChangeLogUpdates("30-12-2016", "1.0.2",
          "UI update:<br>" +
              " - Floating Action Button for changing travel mode<br>" +
              " - Floating Action Button for undo point<br>" +
              " - Undoing a point will center on the last point<br>" +
              " - Added circular progress to change the SnapToMarker state"));
      add(new ChangeLogUpdates("29-12-2016", "1.0.1",
          "New functionality: <br>" +
              " - Added option menu for travel modes <br>" +
              "\t- Driving<br>" +
              "\t- Walking<br>" +
              "\t- Cycling<br>" +
              " - Fixed menu setting persistent state<br>" +
              " - Share App item<br>" +
              " - BETA testing open<br>" +
              " - GPS changed to GoogleApiClient"));
      add(new ChangeLogUpdates("29-12-2016", "1.0.0", "First Release"));
    }
  };

  //For the changelog notice
  public static ChangeLogUpdates getLatest() {
    return list.get(0);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_changelog);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    ChangelogAdapter adapter = new ChangelogAdapter(list, this);
    ListView changeslist = (ListView) findViewById(R.id.changeslist);
    changeslist.setAdapter(adapter);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        this.finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}