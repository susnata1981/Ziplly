package com.ziplly.app.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.HomePresenter;
import com.ziplly.app.client.widget.ITweetWidgetView;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;

/*
 * Community Wall View
 */
public class HomeView extends Composite implements IHomeView {

	private static final String COMMENT_POSTED_SUCCESSFULLY = "Comment posted successfully";
	private static final String COMMENT_POST_FAILED = "Failed to post comment";
	private static final String LIKE_SUCCESSFUL = "Saved";
	private static final String LIKE_UNSUCCESSFUL = "Failed";
	private static final String INVALID_ACCESS = "Invalid access";
	private static final String INTERNAL_ERROR = "Internal error";
	private static final String TWEET_UPDATED_SUCCESSFULLY = "Post updated successfully";
	
	private static HomeViewUiBinder uiBinder = GWT
			.create(HomeViewUiBinder.class);
	
	HomePresenter presenter;
	List<Anchor> filters = new ArrayList<Anchor>();
	
	// tweetId --> TweetWidget
	Map<Long, ITweetWidgetView> tweetWidgetMap = new HashMap<Long, ITweetWidgetView>();
	
	interface HomeViewUiBinder extends UiBinder<Widget, HomeView> {
	}

	public HomeView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		filterHeader.clear();
		HTMLPanel tweetHeader = new HTMLPanel("<div class='filterheader'>Category</span>");
		filterHeader.add(tweetHeader);
		
		for(TweetType type : TweetType.values()) {
			tweetCategoryList.addItem(type.name().toLowerCase());
		}
		
		for(final TweetType type : TweetType.values()) {
			final Anchor anchor = new Anchor(type.name().toLowerCase());
			anchor.setStyleName("tweetFilterLink");
			anchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for(Anchor a : filters) {
						a.getElement().getStyle().setBackgroundColor("");
					}
					anchor.getElement().getStyle().setBackgroundColor("#c2c2c2");
					presenter.displayTweetsForCategory(type);
				}
			});
			filterTweetsPanel.add(anchor);
			filters.add(anchor);
		}
		
		message.setAnimation(true);
//		Window.addWindowScrollHandler(new ScrollHandler() {
//			@Override
//			public void onWindowScroll(ScrollEvent event) {
//				System.out.println("top="+event.getScrollTop()+" clientHeight="+Window.getClientHeight()+
//						"ch="+Document.get().getScrollHeight());
//			}
//		});
		tweetCategoryPanel.getElement().getStyle().setDisplay(Display.NONE);
		tweetTextBox.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				tweetCategoryPanel.getElement().getStyle().setDisplay(Display.INLINE);
			}
		});
		
		cancelBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				tweetCategoryPanel.getElement().getStyle().setDisplay(Display.NONE);
			}
		});
	}

	@UiField
	Alert message;
	
	@UiField
	HTMLPanel filterTweetsPanel;
	
	@UiField
	HTMLPanel communityWallPanel;
	
	@UiField
	TextArea tweetTextBox;
	@UiField
	HTMLPanel tweetCategoryPanel;
	@UiField
	ListBox tweetCategoryList;
	
	@UiField
	Button tweetBtn;
	@UiField
	Button cancelBtn;
	
	@Override
	public void setPresenter(HomePresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
	}

	@UiField
	HTMLPanel filterHeader;
	
	@Override
	public void display(List<TweetDTO> tweets) {
		long time1 = System.currentTimeMillis();
		communityWallPanel.clear();
		message.clear();
		message.setVisible(false);
		for(TweetDTO tweet: tweets) {
			long time11 = System.currentTimeMillis();
			TweetWidget tw = new TweetWidget();
			long time12 = System.currentTimeMillis();
			tw.setPresenter(presenter);
			tw.displayTweet(tweet);
			long time13 = System.currentTimeMillis();
			communityWallPanel.add(tw);
			long time14 = System.currentTimeMillis();
			
			System.out.println("Time to create tweet widget: "+(time12-time11)+" to render: "+(time13-time12)+" to addToPanel: "+(time14-time13));
			tweetWidgetMap.put(tweet.getTweetId(), tw);
		}
		
		long time2 = System.currentTimeMillis();
		System.out.println("Time to render wall: "+(time2-time1));
	}
	
	@Override
	public void displayCommentSuccessfull() {
		displaySuccessMessage(COMMENT_POSTED_SUCCESSFULLY);
	}
	
	@Override
	public void displayCommentFailure() {
		displayErrorMessage(COMMENT_POST_FAILED);
	}

	@Override
	public void displayLikeSuccessful() {
		displaySuccessMessage(LIKE_SUCCESSFUL);
	}

	@Override
	public void displayLikeUnsuccessful() {
		System.out.println("Duplicate like...");
		displayErrorMessage(LIKE_UNSUCCESSFUL);
	}

	private void displayErrorMessage(String msg) {
		message.setText(msg);
		message.setType(AlertType.ERROR);
		message.setVisible(true);
	}
	
	private void displaySuccessMessage(String msg) {
		message.setText(msg);
		message.setType(AlertType.SUCCESS);
		message.setVisible(true);	
	}
	
	@Override
	public void updateTweet(TweetDTO tweet) {
		if (tweet == null || tweet.getTweetId() == null) {
			throw new IllegalArgumentException();
		}
		
		ITweetWidgetView tw = tweetWidgetMap.get(tweet.getTweetId());
		if (tw == null) {
			// do nothing
			return;
		}
		tw.displayTweet(tweet);
	}

	@Override
	public void displayInvalidAccessMessage() {
		displayErrorMessage(INVALID_ACCESS);
	}

	@Override
	public void displayInternalError() {
		displayErrorMessage(INTERNAL_ERROR);
	}

	@Override
	public void updateComment(CommentDTO comment) {
		if (comment == null) {
			throw new IllegalArgumentException();
		}
		
		ITweetWidgetView tw = tweetWidgetMap.get(comment.getTweet().getTweetId());
		tw.updateComment(comment);
	}

	@Override
	public void displayTweetUpdated() {
		displaySuccessMessage(TWEET_UPDATED_SUCCESSFULLY);
	}

	@Override
	public void updateTweetLike(LoveDTO like) {
		if (like == null) {
			throw new IllegalArgumentException();
		}
		
		ITweetWidgetView tw = tweetWidgetMap.get(like.getTweet().getTweetId());
		tw.updateLike(like);
	}
	
	@UiHandler("tweetBtn")
	void tweet(ClickEvent event) {
		if (tweetTextBox.getText() != null) {
			TweetDTO tweet = new TweetDTO();
			String content = tweetTextBox.getText().trim();
			tweet.setContent(content);
			TweetType tweetType = TweetType.values()[tweetCategoryList
					.getSelectedIndex()];
			tweet.setType(tweetType);
			tweet.setTimeCreated(new Date());
			presenter.tweet(tweet);
			tweetCategoryPanel.getElement().getStyle().setDisplay(Display.NONE);
			tweetTextBox.setText("");
		}
	}
}
