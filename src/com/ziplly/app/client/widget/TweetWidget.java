package com.ziplly.app.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;

public class TweetWidget extends Composite implements ITweetWidgetView<TweetPresenter> {

	private static final int PROFILE_COUNT_FOR_LIKE = 2;
	private static final int DEFAULT_COMMENT_COUNT = 4;

	private static TweetWidgetUiBinder uiBinder = GWT
			.create(TweetWidgetUiBinder.class);

	interface TweetWidgetUiBinder extends UiBinder<Widget, TweetWidget> {
	}

	interface Style extends CssResource {
		String tweetCommentSection();

		String comment();
		
		String smalltext();

		String profileImageForComment();
	}

	@UiFactory
	ZResources getResources() {
		ZResources.IMPL.style().ensureInjected();
		return ZResources.IMPL;
	}
	
	@UiField
	Style style;

	// Tweet Section
	@UiField
	HTMLPanel tweetPanel;
	@UiField
	Image authorImage;
	@UiField
	Anchor authorProfileLink;
	@UiField
	Element authorName;
	@UiField
	Element tweetContentSpan;

	@UiField
	TextArea tweetContentTextArea;
	@UiField
	HTMLPanel tweetEditButtonPanel;
	@UiField
	Button saveBtn;
	@UiField
	Button cancelBtn;

	@UiField
	NavLink editTweetLink;
	@UiField
	NavLink deleteTweetLink;
	@UiField
	NavLink reportSpamLink;

	@UiField
	Element timeCreated;

	// Like section
	@UiField
	Anchor likeTweetLink;
	@UiField
	HTMLPanel tweetLikePanel;
	List<LoveDTO> likes = new ArrayList<LoveDTO>();
	ArrayList<HorizontalPanel> comments = new ArrayList<HorizontalPanel>();

	// Comment section
	@UiField
	Anchor commentLink;
	@UiField
	HTMLPanel tweetCommentSection;
	@UiField
	TextArea commentInputTextBox;
	@UiField
	Button commentBtn;
	@UiField
	Button cancelCommentBtn;

	private TweetPresenter presenter;
	private TweetDTO tweet;
	final Modal modal = new Modal();
	final Anchor showMoreCommentsLink = new Anchor();
	final Anchor hideCommentsLink = new Anchor("hide comments");
	
	// private IAccountWidgetModal<PersonalAccountDTO>
	// personalAccountWidgetModal;

	public TweetWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		hideTweetUpdateButtons();
		hideCommentButtons();
		setupHandlers();
		// personalAccountWidgetModal = new PersonalAccountWidgetModal();
		modal.setAnimation(true);
		modal.setWidth("20%");
		modal.setCloseVisible(true);
		modal.setTitle("people who liked this post");
	}

	private void setupHandlers() {
		editTweetLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showTweetUpdateButtons();
			}
		});

		deleteTweetLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean confirm = Window.confirm("Are you sure you want to delete this?");
				if (confirm) {
					presenter.deleteTweet(tweet);
				}
			}
		});
		
		saveBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				tweet.setContent(tweetContentTextArea.getText());
				presenter.updateTweet(tweet);
				tweetContentSpan.getStyle().setVisibility(Visibility.VISIBLE);
//				tweetContentTextArea.getElement().getStyle()
//						.setVisibility(Visibility.HIDDEN);
//				saveBtn.setVisible(false);
//				cancelBtn.setVisible(false);
			}
		});

		cancelBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hideTweetUpdateButtons();
			}
		});

		commentInputTextBox.setPlaceholder("Comment...");
		commentLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				focusOnCommentBox();
			}
		});

		commentInputTextBox.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				focusOnCommentBox();
			}
		});

		commentBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				CommentDTO comment = new CommentDTO();
				comment.setContent(commentInputTextBox.getText());
				comment.setTimeCreated(new Date());
				comment.setAuthor(tweet.getSender());
				comment.setTweet(tweet);
				presenter.postComment(comment);
				commentInputTextBox.setText("");
			}
		});

		cancelCommentBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hideCommentButtons();
			}
		});
	}

	private void focusOnCommentBox() {
		commentInputTextBox.setHeight("28px");
		commentInputTextBox.setFocus(true);
		showCommentButtons();
	}

	void hideTweetUpdateButtons() {
		tweetContentSpan.getStyle().setDisplay(Display.BLOCK);
		tweetContentTextArea.getElement().getStyle().setDisplay(Display.NONE);
		tweetContentTextArea.setReadOnly(true);
		tweetEditButtonPanel.getElement().getStyle().setDisplay(Display.NONE);
	}

	void showTweetUpdateButtons() {
		tweetContentSpan.getStyle().setDisplay(Display.NONE);
		tweetContentTextArea.setText(tweet.getContent());
		tweetContentTextArea.setReadOnly(false);
		tweetContentTextArea.getElement().getStyle().setDisplay(Display.BLOCK);
		tweetEditButtonPanel.getElement().getStyle().setDisplay(Display.BLOCK);
	}

	void hideCommentButtons() {
		commentInputTextBox.setHeight("20px");
		commentBtn.setVisible(false);
		cancelCommentBtn.setVisible(false);
	}

	void showCommentButtons() {
		commentBtn.setVisible(true);
		cancelCommentBtn.setVisible(true);
	}

	/*
	 * Main method, called by HomeView
	 */
	@Override
	public void displayTweet(final TweetDTO tweet) {
		if (tweet != null) {
			this.tweet = tweet;

			hideTweetUpdateButtons();

			hideCommentButtons();

			hideMoreComments();

			// main tweet section
			displayTweetSection();

			// like tooltip
			displayLikesSection();

			// comments
			displayCommentSection();
		}
	}

	private void displayCommentSection() {
		tweetCommentSection.clear();
		comments.clear();
		Collections.sort(tweet.getComments(), new Comparator<CommentDTO>() {
			@Override
			public int compare(CommentDTO c1, CommentDTO c2) {
				return c1.getTimeCreated().before(c2.getTimeCreated()) ? 1 : -1;
			}
		});
		
		int commentCount = 0;
		for (final CommentDTO comment : tweet.getComments()) {
			HorizontalPanel panel = addNextComment(comment);
//			panel.setStyleName(style.comment());
			comments.add(panel);
			tweetCommentSection.add(panel);
			if (commentCount < DEFAULT_COMMENT_COUNT) {
				;
			} else {
				panel.setVisible(false);
			}
			commentCount++;
		}

		// populate show more link
		if (commentCount > DEFAULT_COMMENT_COUNT) {
			showMoreCommentsLink
					.setText(comments.size() + " comments show all");
			showMoreCommentsLink.setStyleName("smalltext");
			showMoreCommentsLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showMoreComments();
				}
			});

			hideCommentsLink.setStyleName("smalltext");
			hideCommentsLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					hideMoreComments();
				}
			});
			tweetCommentSection.add(showMoreCommentsLink);
			tweetCommentSection.add(hideCommentsLink);
		}
	}

	void showMoreComments() {
		for (int i = DEFAULT_COMMENT_COUNT; i < comments.size(); i++) {
			comments.get(i).setVisible(true);
		}
		showMoreCommentsLink.setVisible(false);
		hideCommentsLink.setVisible(true);
	}

	void hideMoreComments() {
		for (int i = DEFAULT_COMMENT_COUNT; i < comments.size(); i++) {
			comments.get(i).setVisible(false);
		}
		showMoreCommentsLink.setVisible(true);
		hideCommentsLink.setVisible(false);
	}

	@Override
	public void addComment(final CommentDTO comment) {
		tweet.getComments().add(comment);
		displayCommentSection();
	}

	private HorizontalPanel addNextComment(final CommentDTO comment) {
		Image pImage = null;
		if (comment.getAuthor().getImageUrl() != null) {
			pImage = new Image(comment.getAuthor().getImageUrl());
		} else {
			pImage = new Image("");
		}
		pImage.addStyleName(style.profileImageForComment());
		pImage.setSize("30px", "25px");
		Hyperlink imageLink = new Hyperlink();
		pImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
//				TweetWidget.this.displayAccountModal(acct);
				displayPublicProfile(comment.getAuthor());
			}
		});
		imageLink.getElement().getFirstChild().appendChild(pImage.getElement());
		imageLink.setWidth("40px");
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(imageLink);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		HTMLPanel commentText = new HTMLPanel("<span class='tinytext'>"
				+ comment.getContent() + "</span>");
		commentText.setWidth("80%");
		panel.add(commentText);
		panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		HTMLPanel commentDate = new HTMLPanel("<span class='datefont'>"
				+ getFormattedTime(comment.getTimeCreated()) + "</span>");
		panel.add(commentDate);
		panel.setStyleName(style.tweetCommentSection());
		return panel;
	}

	<T extends AccountDTO> void displayAccountModal(T acct) {
//		@SuppressWarnings("unchecked")
//		IAccountWidgetModal<T> accountWidgetModal = (IAccountWidgetModal<T>) WidgetFactory
//				.getAccountWidgetModal(acct, presenter);
//		accountWidgetModal.show(acct);
	}

	private void displayLikesSection() {
		// like click handler
		likeTweetLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				System.out.println("Clicked on like tweet:"
						+ tweet.getTweetId());
				presenter.likeTweet(tweet.getTweetId());
			}
		});

		if (tweet.getLikes().size() == 0) {
			tweetLikePanel.getElement().getStyle().setDisplay(Display.NONE);
			return;
		}

		tweetLikePanel.getElement().getStyle().setDisplay(Display.BLOCK);
		int likesCounter = 0;
		modal.hide();
		tweetLikePanel.clear();
		HorizontalPanel hp = new HorizontalPanel();
		HTMLPanel modalPanel = new HTMLPanel("");
		for (final LoveDTO like : tweet.getLikes()) {
			Anchor profileLink = new Anchor(like.getAuthor().getDisplayName());
			profileLink.setStyleName(style.smalltext());
			profileLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
//					displayAccountModal(like.getAuthor());
					displayPublicProfile(like.getAuthor());
				}
			});

			if (likesCounter < PROFILE_COUNT_FOR_LIKE) {
				if (likesCounter > 0) {
					hp.add(new HTMLPanel("&nbsp;,"));
				}
				hp.add(profileLink);
			}
			likesCounter++;
			likes.add(like);
			profileLink = new Anchor(like.getAuthor().getDisplayName());

			// TODO handler not working
			profileLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					modal.hide();
					displayAccountModal(like.getAuthor());
				}
			});
			modalPanel.add(profileLink);
		}

		modal.add(modalPanel);

		if (likesCounter > 0) {
			hp.add(new HTMLPanel(
					"<span class='smalltext'>&nbsp;likes this</span>&nbsp;"));
			tweetLikePanel.add(hp);
		}

		if (likesCounter >= PROFILE_COUNT_FOR_LIKE) {
			Anchor moreProfileLikeLink = new Anchor("more");
			hp.add(new HTMLPanel("&nbsp;"));
			hp.add(moreProfileLikeLink);

			moreProfileLikeLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					modal.toggle();
				}
			});
		}
	}

	private void displayTweetSection() {
		tweetContentSpan.setInnerHTML(tweet.getContent());
		tweet.getTimeCreated();

		String time = getFormattedTime(tweet.getTimeCreated());
		timeCreated.setInnerText(time);

		if (tweet.getSender().getImageUrl() != null) {
			authorImage.setUrl(tweet.getSender().getImageUrl());
		} else {
			authorImage.setUrl(ZResources.IMPL.noImage().getSafeUri());
		}
		authorName.setInnerText(tweet.getSender().getDisplayName());
	}

	@UiHandler("authorProfileLink")
	void displayProfile(ClickEvent event) {
//		displayAccountModal(tweet.getSender());
		displayPublicProfile(tweet.getSender());
	}
	
	private void displayPublicProfile(AccountDTO account) {
		presenter.goTo(new PersonalAccountPlace(account.getAccountId()));
	}

	@Override
	public void setPresenter(TweetPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void clear() {
		commentInputTextBox.setText("");
	}

	@Override
	public void updateTweet(TweetDTO tweet) {
		displayTweet(tweet);
	}

	@Override
	public void deleteTweet() {
		presenter.deleteTweet(tweet);
	}

	String getFormattedTime(Date date) {
		return DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG)
				.format(date);
	}

	@Override
	public void updateComment(CommentDTO comment) {
		tweet.getComments().add(comment);
		displayCommentSection();
	}

	@Override
	public void updateLike(LoveDTO like) {
		tweet.getLikes().add(like);
		displayLikesSection();
	}

	@Override
	public void setWidth(String width) {
		tweetPanel.setWidth(width);
	}
	
	@Override
	public void remove() {
		removeFromParent();
	}
}
