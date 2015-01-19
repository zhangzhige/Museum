package com.waltz3d.museum;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class FootListView extends ListView {

	private View mFootView;

	private TextView tv_loadingtip;

	private ProgressBar progress;

	private LinearLayout loading_layout;

	private FootStatus footStatus;

	private OnLoadMoreListenr mOnLoadMoreListenr;

	private OnScrollListener mOnScrollListener = new OnScrollListener() {

		boolean can_touch = true;

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case SCROLL_STATE_TOUCH_SCROLL:
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				int count = view.getCount();
				int last = view.getLastVisiblePosition();
				if ((last == (count - 1)) && can_touch && footStatus == FootStatus.Common) {
					mFootView.performClick();
				}
			default:
				break;
			}
		}

		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			can_touch = (visibleItemCount != totalItemCount);
		}
	};

	public void setOnLoadMoreListenr(OnLoadMoreListenr mOnLoadMoreListenr) {
		this.mOnLoadMoreListenr = mOnLoadMoreListenr;
	}

	public FootListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnScrollListener(mOnScrollListener);
		mFootView = LayoutInflater.from(context).inflate(R.layout.listview_foot_view, this, false);
		tv_loadingtip = (TextView) mFootView.findViewById(R.id.tv_loadingtip);
		progress = (ProgressBar) mFootView.findViewById(R.id.progress_loading);
		loading_layout = (LinearLayout) mFootView.findViewById(R.id.loading_layout);
		mFootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mOnLoadMoreListenr != null) {
					mOnLoadMoreListenr.onLoadData();
				}
				setFootStatus(FootStatus.Loading);
			}
		});
		addFooterView(mFootView);
	}

	public void setFootStatus(FootStatus status) {
		if (footStatus == status) {
			return;
		}
		footStatus = status;
		switch (status) {
		case Loading:
			loading_layout.setVisibility(View.VISIBLE);
			progress.setVisibility(View.VISIBLE);
			tv_loadingtip.setText(R.string.loading);
			break;
		case Error:
			loading_layout.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			tv_loadingtip.setText(R.string.click_to_retry);
			break;
		case Gone:
			loading_layout.setVisibility(View.GONE);
			break;
		case Common:
			loading_layout.setVisibility(View.VISIBLE);
			progress.setVisibility(View.GONE);
			tv_loadingtip.setText(R.string.loading_more);
			break;
		default:
			break;
		}
	}

	public enum FootStatus {
		Loading, // 加载状态
		Error, // 出错状态
		Gone, // 不可见
		Common// 普通状态
	}

	public interface OnLoadMoreListenr {
		void onLoadData();
	}
}
