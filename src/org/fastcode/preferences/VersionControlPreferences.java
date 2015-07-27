package org.fastcode.preferences;

import static org.fastcode.common.FastCodeConstants.FAST_CODE_PLUGIN_ID;
import static org.fastcode.common.FastCodeConstants.SPACE;
import static org.fastcode.preferences.PreferenceConstants.P_CHECK_IN;
import static org.fastcode.preferences.PreferenceConstants.P_COMMENTS_FOOTER;
import static org.fastcode.preferences.PreferenceConstants.P_COMMENTS_PREFIX;
import static org.fastcode.preferences.PreferenceConstants.P_ENABLE_AUTO_CHECKIN;
import static org.fastcode.preferences.PreferenceConstants.P_ENABLE_TRACK_ECLIPSE_CHANGE;
import static org.fastcode.preferences.PreferenceConstants.P_EXCLUDE_DIR;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSIROTY_PROJECT_URL_PAIR;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSITORY_NAME;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSITORY_PASSWORD;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSITORY_URL;
import static org.fastcode.preferences.PreferenceConstants.P_REPOSITORY_USERNAME;
import static org.fastcode.preferences.PreferenceConstants.P_TIME_GAP_BEFORE_CHECK_IN;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import java.util.Map;
import java.util.HashMap;
import static org.fastcode.preferences.VersionControlPreferencePage.getPrjUrlPairMap;

public class VersionControlPreferences {

	private boolean								enable;
	private String								name;
	private String								url;
	private String								userId;
	private String								password;
	private String								timeGap;
	private String								checkIn;
	private String								comntPrefix;
	private Map<String, String>					prjUrlPair					= new HashMap<String, String>();
	private String								prjUrlStr;
	private String								comntFooter;
	private static boolean						reload;
	private static VersionControlPreferences	versionControlPreferences	= new VersionControlPreferences();
	private final IPreferenceStore				preferenceStore;
	private boolean								enableResChngListener;
	private String[]								excludeDir;


	private VersionControlPreferences() {
		this.preferenceStore = new ScopedPreferenceStore(new InstanceScope(), FAST_CODE_PLUGIN_ID);
		load();
	}

	private void load() {
		this.enable = this.preferenceStore.getBoolean(P_ENABLE_AUTO_CHECKIN);
		this.name = this.preferenceStore.getString(P_REPOSITORY_NAME);
		this.url = this.preferenceStore.getString(P_REPOSITORY_URL);
		this.userId = this.preferenceStore.getString(P_REPOSITORY_USERNAME);
		this.password = this.preferenceStore.getString(P_REPOSITORY_PASSWORD);
		this.timeGap = this.preferenceStore.getString(P_TIME_GAP_BEFORE_CHECK_IN);
		this.checkIn = this.preferenceStore.getString(P_CHECK_IN);
		this.comntPrefix = this.preferenceStore.getString(P_COMMENTS_PREFIX);
		this.comntFooter = this.preferenceStore.getString(P_COMMENTS_FOOTER);
		this.prjUrlPair = getPrjUrlPairMap(this.preferenceStore.getString(P_REPOSIROTY_PROJECT_URL_PAIR));
		this.enableResChngListener = this.preferenceStore.getBoolean(P_ENABLE_TRACK_ECLIPSE_CHANGE);
		this.excludeDir =  this.preferenceStore.getString(P_EXCLUDE_DIR).split(SPACE);
	}

	public static void setReload(final boolean aReload) {
		reload = aReload;
	}

	public static VersionControlPreferences getInstance() {
		if (reload) {
			reload = false;
			versionControlPreferences = new VersionControlPreferences();
		}
		return versionControlPreferences;

	}

	/**
	 *
	 * getter method for name
	 * @return
	 *
	 */
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * getter method for userId
	 * @return
	 *
	 */
	public String getUserId() {
		return this.userId;
	}

	/**
	 *
	 * getter method for password
	 * @return
	 *
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 *
	 * getter method for timeGap
	 * @return
	 *
	 */
	public String getTimeGap() {
		return this.timeGap;
	}

	/**
	 *
	 * getter method for checkIn
	 * @return
	 *
	 */
	public String getCheckIn() {
		return this.checkIn;
	}

	/**
	 *
	 * getter method for comntPrefix
	 * @return
	 *
	 */
	public String getComntPrefix() {
		return this.comntPrefix;
	}

	/**
	 *
	 * getter method for comntFooter
	 * @return
	 *
	 */
	public String getComntFooter() {
		return this.comntFooter;
	}

	/**
	 *
	 * getter method for url
	 * @return
	 *
	 */
	public String getUrl() {
		return this.url;
	}

	/**
	 *
	 * setter method for url
	 * @param url
	 *
	 */
	public void setUrl(final String url) {
		this.url = url;
	}

	/**
	 *
	 * getter method for enable
	 * @return
	 *
	 */
	public boolean isEnable() {
		return this.enable;
	}

	/**
	 *
	 * setter method for enable
	 * @param enable
	 *
	 */
	public void setEnable(final boolean enable) {
		this.enable = enable;
	}

	public Map<String, String> getPrjUrlPair() {
		return this.prjUrlPair;
	}

	public void setPrjUrlPair(final Map<String, String> prjUrlPair) {
		this.prjUrlPair = prjUrlPair;
	}

	private void parsePrjUrlStr(final String prjUrlStr) {
		// TODO Auto-generated method stub

	}

	public boolean isEnableResChngListener() {
		return this.enableResChngListener;
	}

	public void setEnableResChngListener(final boolean enableResChngListener) {
		this.enableResChngListener = enableResChngListener;
	}

	public String[] getExcludeDir() {
		return this.excludeDir;
	}

	public void setExcludeDir(final String[] excludeDir) {
		this.excludeDir = excludeDir;
	}
}
