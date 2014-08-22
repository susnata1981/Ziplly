package com.ziplly.app.client.view.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.common.ListenableInteger.PropertyChangeListener;
import com.ziplly.app.client.view.event.TweetSentEvent;
import com.ziplly.app.client.widget.StyleHelper;
import com.ziplly.app.client.widget.TweetWidget;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;

public class TweetListView extends Composite implements ITweetListView {
	private static TweetViewUiBinder uiBinder = GWT.create(TweetViewUiBinder.class);

	interface TweetViewUiBinder extends UiBinder<Widget, TweetListView> {
	}

	interface Style extends CssResource {
		String tweetWidget();
	}

	@UiField
	Style style;

	@UiField
	Alert message;

	@UiField
	FlowPanel tweetsSection;
	
	HTMLPanel tempPanel = new HTMLPanel("");

	// TweetId ---> TweetWidget
	Map<Long, TweetWidget> tweetWidgetMap = new HashMap<Long, TweetWidget>();

	private String tweetWidgetWidth = "68%";

  private TweetWidgetPresenter tweetWidgetPresenter;
  private EventBus eventBus;

	public TweetListView(
	    EventBus eventBus, 
	    TweetWidgetPresenter tweetWidgetPresenter) {
		assert(tweetWidgetPresenter != null);
		this.eventBus = eventBus;

		initWidget(uiBinder.createAndBindUi(this));
		this.tweetWidgetPresenter = tweetWidgetPresenter;
		message.setAnimation(true);
		StyleHelper.show(message.getElement(), false);
		setupEventListeners();
	}

	private void setupEventListeners() {
	  eventBus.addHandler(TweetSentEvent.TYPE, new TweetSentEvent.Handler() {
      
      @Override
      public void onEvent(TweetSentEvent event) {
        insertFirst(event.getTweet());
      }
    });
	  
  }

  @Override
	public void clear() {
		StyleHelper.show(message.getElement(), false);
		tweetsSection.clear();
	}

	@Override
	public void displayNoTweetsMessage() {
		message.setText(StringConstants.TWEET_NOT_POSTED);
		StyleHelper.show(message.getElement(), true);
	}

	@Override
	public void setWidth(String width) {
		tweetWidgetWidth = width;
	}

	@Override
	public void setHeight(String tweetWidgetHeight) {
		tweetsSection.setHeight(tweetWidgetHeight);
	}

	@Override
  public void insertLast(List<TweetDTO> tweets, final RenderingStatusCallback callback) {
	  int totalTweets = tweets.size();
	  if (totalTweets == 0) {
	    callback.finished(100);
	  }
	  
	  ListenableInteger tweetCounter = createTweetListener(totalTweets, callback);
	  
    for (TweetDTO tweet : tweets) {
      addTweet(tweet, tweetCounter);
    }
  }

	private void addTweet(final TweetDTO tweet, final ListenableInteger tweetCounter) {
    Scheduler.get().scheduleDeferred(new Command() {
      
      @Override
      public void execute() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

          @Override
          public void execute() {
            TweetWidget tw = createTweetWidget(tweet);
            tweetsSection.add(tw);
            tweetWidgetMap.put(tweet.getTweetId(), tw);
            tweetCounter.increment();
          }
        });
      }
    });
  }
  
	private ListenableInteger createTweetListener(final int totalTweets, final RenderingStatusCallback callback) {
	  ListenableInteger tweetCounter = new ListenableInteger();
    tweetCounter.addListener(new PropertyChangeListener() {

       @Override
       public void propertyChange(Object oldValue, Object newValue) {
         if ((Integer)newValue == totalTweets) {
           callback.finished(100.0);
         }
       }
       
     });
    
    return tweetCounter;
  }

  @Override
	public void insertFirst(final TweetDTO tweet) {
		Scheduler.get().scheduleDeferred(new Command() {
			@Override
			public void execute() {
			  TweetWidget tw = createTweetWidget(tweet);
				tweetsSection.insert(tw, 0);
				tweetWidgetMap.put(tweet.getTweetId(), tw);
			}
		});
	}

	@Override
	public void remove(TweetDTO tweet) {
		TweetWidget widget = tweetWidgetMap.get(tweet.getTweetId());
		widget.removeFromParent();
	}

	@Override
	public void addComment(CommentDTO comment) {
		TweetWidget tweetWidget = tweetWidgetMap.get(comment.getTweet().getTweetId());
		tweetWidget.addComment(comment);
	}

	@Override
	public void updateComment(CommentDTO comment) {
		TweetWidget tweetWidget = tweetWidgetMap.get(comment.getTweet().getTweetId());
		tweetWidget.updateComment(comment);
	}

	@Override
	public void updateLike(LoveDTO like) {
		TweetWidget tweetWidget = tweetWidgetMap.get(like.getTweet().getTweetId());
		tweetWidget.updateLike(like);
	}

	@Override
	public void updateTweet(TweetDTO tweet) {
		TweetWidget tweetWidget = tweetWidgetMap.get(tweet.getTweetId());
		tweetWidget.updateTweet(tweet);
	}

	public Element getTweetSection() {
		return tweetsSection.getElement();
	}

	@Override
	public void displayMessage(String msg, AlertType type) {
		message.setText(msg);
		message.setType(type);
		StyleHelper.show(message.getElement(), true);
	}

	@Override
  public void refreshTweet(TweetDTO tweet) {
		TweetWidget tweetWidget = tweetWidgetMap.get(tweet.getTweetId());
		tweetWidget.displayTweet(tweet);
  }

	@Override
  public void resetTweets() {
    tweetsSection.clear();
  }
	
	private TweetWidget createTweetWidget(TweetDTO tweet) {
    TweetWidget tw = new TweetWidget(tweetWidgetPresenter);
    tw.displayTweet(tweet);
    tw.setWidth(tweetWidgetWidth);
    tw.addStyleName(style.tweetWidget());
    return tw;
  }
}
