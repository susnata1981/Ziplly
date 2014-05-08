package com.ziplly.app.client.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.ControlGroup;
import com.github.gwtbootstrap.client.ui.HelpInline;
import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.NavLink;
import com.github.gwtbootstrap.client.ui.Popover;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.BackdropType;
import com.github.gwtbootstrap.client.ui.constants.ButtonType;
import com.github.gwtbootstrap.client.ui.constants.ControlGroupType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.ziplly.app.client.activities.TweetPresenter;
import com.ziplly.app.client.places.HomePlace;
import com.ziplly.app.client.places.PersonalAccountPlace;
import com.ziplly.app.client.places.PlaceUtils;
import com.ziplly.app.client.places.TweetDetailsPlace;
import com.ziplly.app.client.resource.ZResources;
import com.ziplly.app.client.view.StringConstants;
import com.ziplly.app.client.view.WidgetFactory;
import com.ziplly.app.client.view.factory.AbstractValueFormatterFactory;
import com.ziplly.app.client.view.factory.AccountFormatter;
import com.ziplly.app.client.view.factory.BasicDataFormatter;
import com.ziplly.app.client.view.factory.ValueFamilyType;
import com.ziplly.app.client.view.factory.ValueType;
import com.ziplly.app.client.widget.blocks.GoogleWalletPayButtonWidget;
import com.ziplly.app.client.widget.blocks.GoogleWalletPostPayButtonHandler;
import com.ziplly.app.model.AccountDTO;
import com.ziplly.app.model.CommentDTO;
import com.ziplly.app.model.CouponDTO;
import com.ziplly.app.model.LoveDTO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.overlay.GoogleWalletFailureResult;
import com.ziplly.app.model.overlay.GoogleWalletSuccessResult;
import com.ziplly.app.shared.FieldVerifier;
import com.ziplly.app.shared.ValidationResult;

public class TweetWidget extends Composite implements ITweetWidgetView<TweetPresenter> {

	private static final int DEFAULT_COMMENT_COUNT = 4;

	private static TweetWidgetUiBinder uiBinder = GWT.create(TweetWidgetUiBinder.class);

	interface TweetWidgetUiBinder extends UiBinder<Widget, TweetWidget> {
	}

	interface Style extends CssResource {
		String tweetCommentSection();

		String comment();

		String smalltext();

		String profileImageForComment();

		String profileLinkAnchor();

		String commentRow();

		String commentRowAnchor();

		String commentRowText();

		String commentRowDate();

		String commentsLink();

		String commentEditLink();

		String tweetImageModal();

		String tweetModalImage();

		String tweetImageModalCloseButton();

		String couponBoxDiv();
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

	//
	// Tweet Image
	//
	@UiField
	HTMLPanel tweetImagePanel;
	@UiField
	Image tweetImage;

	@UiField
	Anchor categoryLink;
	@UiField
	Anchor authorProfileLink;
	@UiField
	Element authorName;
	@UiField
	HTMLPanel tweetContentPanel;

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

	@UiField
	ZAnchor replyAnchor;

	// Like section
	@UiField
	ZAnchor likeAnchor;
	@UiField
	HTMLPanel tweetLikePanel;
	List<LoveDTO> likes = new ArrayList<LoveDTO>();
	Map<Long, Panel> commentsToWidgetMap = new LinkedHashMap<Long, Panel>();

	// Comment section
	@UiField
	ZAnchor commentLink;
	@UiField
	HTMLPanel tweetCommentSection;
	@UiField
	ControlGroup commentInputTextBoxCg;
	@UiField
	HelpInline commentInputHelpInline;
	@UiField
	TextArea commentInputTextBox;
	@UiField
	Button commentBtn;
	@UiField
	Button cancelCommentBtn;

	// Create Link anchor
	@UiField
	ZAnchor createLink;

	private TweetPresenter presenter;
	private TweetDTO tweet;
	private GoogleWalletPayButtonWidget paymentButton;
	final Anchor showMoreCommentsLink = new Anchor();
	final Anchor hideCommentsLink = new Anchor("hide comments");
	private AccountFormatter accountFormatter = (AccountFormatter) AbstractValueFormatterFactory
	    .getValueFamilyFormatter(ValueFamilyType.ACCOUNT_INFORMATION);
	private BasicDataFormatter basicDataFormatter =
	    (BasicDataFormatter) AbstractValueFormatterFactory
	        .getValueFamilyFormatter(ValueFamilyType.BASIC_DATA_VALUE);

	// Used by comment edit widget to display the edit link
	private boolean commentEditLinkClicked = false;

	public TweetWidget() {
		initWidget(uiBinder.createAndBindUi(this));
		hideTweetUpdateButtons();
		hideCommentButtons();
		setupHandlers();
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

		reportSpamLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean confirm = Window.confirm("Are you sure you want to delete this?");
				if (confirm) {
					presenter.reportTweetAsSpam(tweet);
				}
			}
		});

		saveBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				tweet.setContent(tweetContentTextArea.getText());
				presenter.updateTweet(tweet);
				tweetContentPanel.getElement().getStyle().setVisibility(Visibility.VISIBLE);
				// tweetContentTextArea.getElement().getStyle()
				// .setVisibility(Visibility.HIDDEN);
				// saveBtn.setVisible(false);
				// cancelBtn.setVisible(false);
			}
		});

		cancelBtn.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				tweetContentTextArea.setText("");
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

		commentLink.addMouseHoverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				Popover p = configureLearnMorePopover(commentLink, StringConstants.COMMENT_LINK_HOVER_TEXT);
				p.show();
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
				clearErrorOnCommentTextBox();
				if (!validateComment(commentInputTextBox.getText())) {
					return;
				}

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
				clearComment();
				commentInputTextBox.setHeight("20px");
			}
		});

		replyAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SendMessageWidget smw = new SendMessageWidget(tweet.getSender());
				smw.setSubject(PlaceUtils.getPlaceTokenForTweetDetails(tweet));
				smw.setPresenter(presenter);
				smw.show();
			}
		});

		replyAnchor.addMouseHoverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				Popover p =
				    configureLearnMorePopover(replyAnchor, StringConstants.REPLY_ANCHOR_POPOVER_TEXT);
				p.show();
			}
		});

		createLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final ShareTweetLinkModal modal = new ShareTweetLinkModal();
				modal.setTweetLink(PlaceUtils.getPlaceTokenForTweetDetails(tweet));
				modal.getGotoTweetButton().addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						presenter.goTo(new TweetDetailsPlace(tweet.getTweetId()));
						modal.hide();
					}
				});

				modal.show();
			}
		});

		createLink.addMouseHoverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				Popover p =
				    configureLearnMorePopover(createLink, StringConstants.CREATE_LINK_ANCHOR_POPOVER_TEXT);
				p.show();
				p.show();
			}

		});
	}

	private boolean validateComment(String comment) {
		ValidationResult result = FieldVerifier.validateComment(comment);
		if (!result.isValid()) {
			commentInputTextBoxCg.setType(ControlGroupType.ERROR);
			commentInputHelpInline.setText(result.getErrors().get(0).getErrorMessage());
			commentInputHelpInline.setVisible(true);
			return false;
		}
		return true;
	}

	private void clearErrorOnCommentTextBox() {
		commentInputTextBoxCg.setType(ControlGroupType.NONE);
		commentInputHelpInline.setText("");
		commentInputHelpInline.setVisible(false);
	}

	private void focusOnCommentBox() {
		commentInputTextBox.setHeight("50px");
		commentInputTextBox.setFocus(true);
		showCommentButtons();
	}

	void hideTweetUpdateButtons() {
		tweetContentPanel.getElement().getStyle().setDisplay(Display.BLOCK);
		tweetContentTextArea.getElement().getStyle().setDisplay(Display.NONE);
		tweetContentTextArea.setReadOnly(true);
		tweetEditButtonPanel.getElement().getStyle().setDisplay(Display.NONE);
	}

	void showTweetUpdateButtons() {
		// tweetContentSpan.getStyle().setDisplay(Display.NONE);
		StyleHelper.show(tweetContentPanel.getElement(), false);
		tweetContentTextArea.setText(tweet.getContent());
		tweetContentTextArea.setReadOnly(false);
		tweetContentTextArea.getElement().getStyle().setDisplay(Display.BLOCK);
		tweetEditButtonPanel.getElement().getStyle().setDisplay(Display.BLOCK);
	}

	@UiField
	HTMLPanel commentInputActionSection;

	void hideCommentButtons() {
		commentInputActionSection.getElement().getStyle().setDisplay(Display.NONE);
	}

	void showCommentButtons() {
		commentInputActionSection.getElement().getStyle().setDisplay(Display.BLOCK);
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
		commentsToWidgetMap.clear();
		Collections.sort(tweet.getComments(), new Comparator<CommentDTO>() {
			@Override
			public int compare(CommentDTO c1, CommentDTO c2) {
				return c1.getTimeCreated().before(c2.getTimeCreated()) ? 1 : -1;
			}
		});

		int commentCount = 0;
		for (final CommentDTO comment : tweet.getComments()) {
			Panel panel = addNextComment(comment);
			commentsToWidgetMap.put(comment.getCommentId(), panel);
			tweetCommentSection.add(panel);

			// Hide comments
			if (commentCount >= DEFAULT_COMMENT_COUNT) {
				panel.setVisible(false);
			}
			commentCount++;
		}

		// populate show more link
		if (commentCount > DEFAULT_COMMENT_COUNT) {
			showMoreCommentsLink.setText(commentsToWidgetMap.size() + " comments, show all");
			showMoreCommentsLink.addStyleName(style.commentsLink());
			showMoreCommentsLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					showMoreComments();
				}
			});

			hideCommentsLink.addStyleName(style.commentsLink());
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
		Iterator<Entry<Long, Panel>> iterator = commentsToWidgetMap.entrySet().iterator();
		int count = 0;
		while (iterator.hasNext()) {
			Entry<Long, Panel> entry = iterator.next();
			if (count >= DEFAULT_COMMENT_COUNT) {
				commentsToWidgetMap.get(entry.getKey()).setVisible(true);
			}
			count++;
		}

		showMoreCommentsLink.setVisible(false);
		hideCommentsLink.setVisible(true);
	}

	void hideMoreComments() {
		Iterator<Entry<Long, Panel>> iterator = commentsToWidgetMap.entrySet().iterator();
		int count = 0;
		while (iterator.hasNext()) {
			Entry<Long, Panel> entry = iterator.next();
			if (count >= DEFAULT_COMMENT_COUNT) {
				commentsToWidgetMap.get(entry.getKey()).setVisible(false);
			}
			count++;
		}

		showMoreCommentsLink.setVisible(true);
		hideCommentsLink.setVisible(false);
	}

	private Panel addNextComment(final CommentDTO comment) {
		FlowPanel fp = new FlowPanel();
		final CommentWidget commentWidget = new CommentWidget();
		commentWidget
		    .setImage(accountFormatter.format(comment.getAuthor(), ValueType.PROFILE_IMAGE_URL));
		commentWidget.setText(comment.getContent());
		commentWidget.setProfileName(comment.getAuthor().getDisplayName());
		commentWidget.setPostingDate(basicDataFormatter.format(
		    comment.getTimeCreated(),
		    ValueType.DATE_VALUE_MEDIUM));

		commentWidget.getProfileAnchor().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new PersonalAccountPlace(comment.getAuthor().getAccountId()));
			}
		});

		final NavLink editLink = new NavLink();
		editLink.setStyleName(style.commentEditLink());
		editLink.setIcon(IconType.EDIT);
		editLink.setVisible(false);
		MouseHoverPanel contentPanel = commentWidget.getContentPanel();
		contentPanel.add(editLink);

		contentPanel.addMouseOverHandler(new MouseOverHandler() {
			@Override
			public void onMouseOver(MouseOverEvent event) {
				if (!commentEditLinkClicked) {
					editLink.setVisible(true);
				}
			}
		});

		contentPanel.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				editLink.setVisible(false);
			}
		});

		editLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (commentEditLinkClicked) {
					return;
				}
				
				commentEditLinkClicked = true;
				final Element contentElem = commentWidget.getContentElement();
				final MouseHoverPanel contentPanel = commentWidget.getContentPanel();
				final CommentEditWidget editWidget = new CommentEditWidget();
				editWidget.setText(comment.getContent());

				StyleHelper.show(contentElem, false);
				contentPanel.add(editWidget);

				editWidget.getSaveButton().addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						editWidget.showMessage(false);
						if (!editWidget.validateInput()) {
							return;
						}
						// Save and cancel
						StyleHelper.show(editLink.getElement(), false);
						StyleHelper.show(contentElem, true);
						contentPanel.remove(editWidget);
						commentEditLinkClicked = false;
						comment.setContent(editWidget.getText());
						comment.setTweet(TweetWidget.this.tweet);
						comment.setTimeUpdated(new Date());
						presenter.updateComment(comment);
					}
				});

				editWidget.getCancelButton().addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						StyleHelper.show(editLink.getElement(), false);
						StyleHelper.show(contentElem, true);
						contentPanel.remove(editWidget);
						commentEditLinkClicked = false;
					}
				});
			}
		});

		fp.add(commentWidget);
		return fp;
	}

	<T extends AccountDTO> void displayAccountModal(T acct) {
		@SuppressWarnings("unchecked")
		IAccountWidgetModal<T> accountWidgetModal =
		    (IAccountWidgetModal<T>) WidgetFactory.getAccountWidgetModal(acct, presenter);
		accountWidgetModal.show(acct);
	}

	private void displayLikesSection() {
		// like click handler
		likeAnchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				System.out.println("Clicked on like tweet:" + tweet.getTweetId());
				presenter.likeTweet(tweet.getTweetId());
			}
		});

		tweetLikePanel.getElement().getStyle().setDisplay(Display.BLOCK);
		int likesCounter = tweet.getLikes().size();

		if (likesCounter == 0) {
			return;
		}

		tweetLikePanel.clear();
		StringBuilder likeList = new StringBuilder();
		likes.clear();
		for (final LoveDTO like : tweet.getLikes()) {
			likeList.append(like.getAuthor().getDisplayName() + "<br/>");
			likes.add(like);
		}

		if (likesCounter > 0) {
			final Anchor likesCounterAnchor = new Anchor();
			String word = likesCounter > 1 ? "people" : "person";
			likesCounterAnchor.getElement().setInnerHTML(
			    likesCounter + "<span class='smalltext'>&nbsp;" + word + " like this</span>");
			tweetLikePanel.add(likesCounterAnchor);
			likesCounterAnchor.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					displayPeopleLikeListModal();
				}
			});

			Popover p = new Popover();
			p.setWidget(likesCounterAnchor);
			p.setHeading("People who liked this post");
			p.setText(likeList.toString());
			p.setHtml(true);
			p.reconfigure();
		}
	}

	// TODO : profile link doesn't work inside Modal ???
	private void displayPeopleLikeListModal() {
		final Modal modal = new Modal();
		modal.setAnimation(true);
		modal.setWidth("28%");
		modal.setCloseVisible(true);
		modal.setTitle("people who liked this post");
		for (final LoveDTO like : tweet.getLikes()) {
			AccountDTO acct = like.getAuthor();
			String content = accountFormatter.format(acct, ValueType.PROFILE_IMAGE_URL);
			Anchor profileLink = new Anchor();
			profileLink.getElement().setInnerHTML(content);
			profileLink.setStyleName(style.profileLinkAnchor());
			profileLink.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					displayPublicProfile(like.getAuthor());
				}
			});
			likes.add(like);
			modal.add(profileLink);
		}
		modal.toggle();
	}

	private void displayTweetSection() {
		StyleHelper.show(tweetContentPanel.getElement(), true);
		tweetContentPanel.getElement().setInnerHTML(basicDataFormatter.format(tweet.getContent(), ValueType.TEXT_VALUE));
		
		if (tweet.getCoupon() != null) {
			displayCoupon(tweet.getCoupon());
		}

		categoryLink.setHTML(basicDataFormatter.format(tweet.getType(), ValueType.TWEET_TYPE));
		categoryLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new HomePlace(tweet.getType()));
			}
		});

		String time = basicDataFormatter.format(tweet.getTimeCreated(), ValueType.DATE_VALUE_MEDIUM);
		timeCreated.setInnerHTML(time);

		authorImage.setUrl(accountFormatter.format(tweet.getSender(), ValueType.PROFILE_IMAGE_URL));
		authorImage.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.goTo(new PersonalAccountPlace(tweet.getSender().getAccountId()));
			}
		});
		authorName.setInnerHTML(tweet.getSender().getDisplayName());

		displayTweetImageIfPresent();
	}

	private void displayCoupon(CouponDTO coupon) {
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(style.couponBoxDiv());
		panel.add(new HTMLPanel(basicDataFormatter.format(tweet.getCoupon(), ValueType.COUPON)));
		Button buyCouponBtn = new Button("Buy Now");
		buyCouponBtn.setType(ButtonType.PRIMARY);
		
		paymentButton = new GoogleWalletPayButtonWidget(buyCouponBtn, new GoogleWalletPostPayButtonHandler() {
			
			@Override
			public void onSuccess(GoogleWalletSuccessResult result) {
//				presenter.purchaseCoupon(result.getRequest().getTransactionId() + "", 
//						PurchasedCouponAction.ResultStatus.SUCCESS, tweet.getCoupon());
				Window.alert("Purchase complete");
			}
			
			@Override
			public void onFailure(GoogleWalletFailureResult result) {
//				presenter.purchaseCoupon(result.getRequest().getRequest().getTransactionId() + "", 
//						PurchasedCouponAction.ResultStatus.FAILED, tweet.getCoupon());
				Window.alert("Purchase failed");
			}
		});
		
		paymentButton.addClickHandler(new ClickHandler() {

			@Override
      public void onClick(ClickEvent event) {
				presenter.checkCouponPurchaseEligibility(tweet.getCoupon(), TweetWidget.this);
      }
		});
		
		panel.add(buyCouponBtn);
		tweetContentPanel.add(panel);
  }

	/**
	 * Called by the presenter once {@code checkForBuyerEligiility} returns success.
	 * @param jwtToken 
	 */
	public void initiatePay(String jwtToken) {
		if (paymentButton != null) {
			paymentButton.pay(jwtToken);
		}
	}
	
	private void displayTweetImageIfPresent() {
		if (tweet.getImages().size() > 0) {
			tweetImage.setUrl(tweet.getImages().get(0).getUrl());

			tweetImage.addLoadHandler(new LoadHandler() {

				@Override
				public void onLoad(LoadEvent event) {
					int height = tweetImage.getHeight() + 20;
					tweetImagePanel.setHeight(height + "px");
				}
			});

			tweetImage.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					displayImageInModal(tweet.getImages().get(0).getUrl());
				}

			});

		} else {
			displayElement(tweetImagePanel.getElement(), false);
		}
	}

	private void displayImageInModal(String imageUrl) {
		final Modal imageModal = new Modal();
		imageModal.addStyleName(style.tweetImageModal());
		final FlowPanel panel = new FlowPanel();
		final Image image = new Image(imageUrl);
		image.addStyleName(style.tweetModalImage());
		panel.add(image);
		imageModal.add(panel);
		imageModal.setBackdrop(BackdropType.NORMAL);
		imageModal.setAnimation(true);
		imageModal.setKeyboard(true);
		imageModal.setCloseVisible(true);
		imageModal.show();
		Button closeBtn = new Button("close");
		closeBtn.addStyleName(style.tweetImageModalCloseButton());
		imageModal.add(closeBtn);
		closeBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				imageModal.hide();
			}

		});
	}

	@UiHandler("authorProfileLink")
	void displayProfile(ClickEvent event) {
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
		clearComment();
	}

	private void clearComment() {
		commentInputTextBoxCg.setType(ControlGroupType.NONE);
		commentInputHelpInline.setVisible(false);
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

	@Override
	public void updateComment(CommentDTO comment) {
		FlowPanel panel = (FlowPanel) commentsToWidgetMap.get(comment.getCommentId());
		if (panel != null) {
			CommentWidget commentWidget = (CommentWidget) panel.getWidget(0);
			commentWidget.setText(comment.getContent());
		}
	}

	@Override
	public void addComment(final CommentDTO comment) {
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

	private void displayElement(Element elem, boolean display) {
		Display d = display ? Display.BLOCK : Display.NONE;
		elem.getStyle().setDisplay(d);
	}

	private Popover configureLearnMorePopover(Widget widget, String popoverText) {
		Popover p = new Popover();
		p.setText(popoverText);
		p.setAnimation(true);
		p.setHeading(StringConstants.LEARN_MORE_TEXT);
		p.setPlacement(Placement.RIGHT);
		p.setWidget(widget);
		p.reconfigure();
		return p;
	}
}
