package com.my_company.app_template;

import android.app.ListActivity;
import android.view.View;

public class SaveRestoreListViewItemIndex {

	private int listItemIndex = 0;

	public void setListItemIndex(int listItemIndex) {
		this.listItemIndex = listItemIndex;
	}

	public void setListTopIndex(int listTopIndex) {
		this.listTopIndex = listTopIndex;
	}

	public int getListItemIndex() {
		return listItemIndex;
	}

	public int getListTopIndex() {
		return listTopIndex;
	}

	private int listTopIndex = 0;
	private ListActivity _listActivity;

	SaveRestoreListViewItemIndex(ListActivity listActivity) {
		this._listActivity = listActivity;
	}

	protected void saveListItemIndex() {
		if (this._listActivity == null)
			return;

		// save index and top position
		listItemIndex = this._listActivity.getListView()
				.getFirstVisiblePosition();
		View v = this._listActivity.getListView().getChildAt(0);
		listTopIndex = (v == null) ? 0 : v.getTop();
	}

	protected void restoreSavedListItemIndex() {
		if (this._listActivity == null)
			return;

		// restore
		this._listActivity.getListView().setSelectionFromTop(listItemIndex,
				listTopIndex);
	}

	protected void resetListItemIndex() {
		if (this._listActivity == null)
			return;

		// reset
		View v = this._listActivity.getListView().getChildAt(0);
		listTopIndex = (v == null) ? 0 : v.getTop();
		this._listActivity.getListView().setSelectionFromTop(0, listTopIndex);
	}
}
