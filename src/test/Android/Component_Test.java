package test.Android;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class Component_Test extends TabActivity {
    /** Called when the activity is first created. */
	public static TabActivity ty;
	private static TabSpec tsArtistsEntries = null;
	private static TabSpec tsAlbumsEntries = null;
	private static TabSpec tsSongsEntries = null;
	
	private static final String ARTISTS_TAB_NAME = "ARTISTS";
	private static final String ALBUMS_TAB_NAME = "ALBUMS";
	private static final String SONGS_TAB_NAME = "SONGS";
	
	private final TabHost mytabHost = getTabHost();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        try{
                   
          //create TabSpec instances
          tsArtistsEntries = mytabHost.newTabSpec(ARTISTS_TAB_NAME);
          tsAlbumsEntries = mytabHost.newTabSpec(ALBUMS_TAB_NAME);
          tsSongsEntries = mytabHost.newTabSpec(SONGS_TAB_NAME);
          
    //create page tabs
          //artists tab
          Intent intentArtists = new Intent(this, ArtistsActivity.class);
    
          mytabHost.addTab(tsArtistsEntries.setIndicator(ARTISTS_TAB_NAME, 
                                                this.getResources()
                                                .getDrawable(R.drawable.ic_tab_artists))
                   .setContent(intentArtists));

        //albums tab
          Intent intentAlbums = new Intent(this, AlbumsActivity.class);
          
          mytabHost.addTab(tsAlbumsEntries.setIndicator(ALBUMS_TAB_NAME, 
                                                this.getResources()
                                                .getDrawable(R.drawable.ic_tab_albums))
                  .setContent(intentAlbums));
          
        //songs tab
          Intent intentSongs = new Intent(this, SongsActivity.class);
          
          mytabHost.addTab(tsSongsEntries.setIndicator(SONGS_TAB_NAME, 
                                                this.getResources()
                                                .getDrawable(R.drawable.ic_tab_songs))
                   .setContent(intentSongs));
          
          int intTabChildCount = mytabHost.getTabWidget().getChildCount();
          
          for(int i = 0; i < intTabChildCount; i++)
          {
        	  mytabHost.getTabWidget().getChildAt(i).setBackgroundColor(R.color.lightyellow);
          }

          }//end try statements
          catch (Exception error){
            MySDCErrorLog<Exception> errExcpError = new MySDCErrorLog<Exception>(this);
            errExcpError.addToLogFile(error, "Component_Test.onCreate", "");
            errExcpError = null;
          }//end try/catch (Exception error)
      }//end onCreate
      
      
      /**
       * Sets object variables to null
       * 
       * @return void
       * 
       */
      protected static void myAppCleanup(){
        /* Set object variables to null */
        //mReportsEntryCursor = null;
      }//end myAppCleanup
      
      
      
      /**
       * Called after onRestoreInstanceState(Bundle), onRestart(), 
       * or onPause(), for your activity to start interacting with 
       * the user. 
       * 
       * This is a good place to begin animations, open 
       * exclusive-access devices (such as the camera), etc. 
       * 
       * The data is refreshed
       * 
       */
      @Override
      protected void onResume() {
       
        super.onResume();    
      }//end onResume
      
      
      /**
       * Called as part of the activity lifecycle when an activity is going into the 
       * background, but has not (yet) been killed. The counterpart to onResume(). 
       * 
       * This callback is mostly used for saving any persistent state the activity is editing, 
       * to present a "edit in place" model to the user and making sure nothing is lost if 
       * there are not enough resources to start the new activity without first killing this one. 
       * 
       * This is also a good place to do things like stop animations and other things that consume 
       * a noticeable mount of CPU in order to make the switch to the next activity as fast as 
       * possible, or to close resources that are exclusive access such as the camera. 
       * 
       * Checks the current state.  If in "QUIT" state,
       * then the cleanup and finish code is executed.
       *  
       * "SAVEINSTANCE" is used for when the screen orientation is changed.
       * 
       * @return void
       * 
       */
      @Override
      protected void onPause() {
          // The user is going somewhere else, so make sure their current
          // changes are safely saved away in the provider.  We don't need
          // to do this if only editing.
          myAppCleanup();
          
          /* call System.gc */
          System.gc();
          finish();
          
          super.onPause();
      }//end onPause
      
      
      /**
       * onDestroy method
       * 
       * Perform any final cleanup before an activity is destroyed. 
       * 
       * This can happen either because the activity is finishing (someone called finish() on it, 
       * or because the system is temporarily destroying this instance of the activity to save space. 
       * 
       * You can distinguish between these two scenarios with the isFinishing() method. 
       * 
       * Note: do not count on this method being called as a place for saving data! For example, if an 
       * activity is editing data in a content provider, those edits should be committed in either 
       * onPause() or onSaveInstanceState(Bundle), not here. 
       * 
       * This method is usually implemented to free resources like threads that are associated with an 
       * activity, so that a destroyed activity does not leave such things around while the rest of its 
       * application is still running. 
       * 
       * There are situations where the system will simply kill the activity's hosting process without 
       * calling this method (or any others) in it, so it should not be used to do things that are intended 
       * to remain around after the process goes away. 
       * 
       * Derived classes must call through to the super class's implementation of this method. If they do 
       * not, an exception will be thrown.
       * 
       * @return void
       * 
       */
      @Override
      protected void onDestroy() {
          // The user is going somewhere else, so make sure their current
          // changes are safely saved away in the provider.  We don't need
          // to do this if only editing.       
        myAppCleanup();
        
        super.onDestroy();
      }
      
      /*
      public void onTabChanged(String tabId) {
        // TODO Auto-generated method stub
    	  for(int i = 0; i < mytabHost.getTabWidget().getChildCount(); i++)
        {
        	mytabHost.getTabWidget().getChildAt(i).setBackgroundColor(R.color.lightyellow);
        }

        mytabHost.getTabWidget().getChildAt(mytabHost.getCurrentTab()).setBackgroundColor(R.color.lightgreen);
      }//end onTabChanged
      */

    }//end Component_Test
