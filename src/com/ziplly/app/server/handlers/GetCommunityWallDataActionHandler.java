package com.ziplly.app.server.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import net.customware.gwt.dispatch.server.ExecutionContext;
import net.customware.gwt.dispatch.shared.DispatchException;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.ziplly.app.client.exceptions.NotFoundException;
import com.ziplly.app.dao.AccountDAO;
import com.ziplly.app.dao.HashtagDAO;
import com.ziplly.app.dao.SessionDAO;
import com.ziplly.app.dao.TweetDAO;
import com.ziplly.app.model.TweetDTO;
import com.ziplly.app.model.TweetType;
import com.ziplly.app.server.bli.AccountBLI;
import com.ziplly.app.server.bli.TweetBLI;
import com.ziplly.app.shared.GetCommunityWallDataAction;
import com.ziplly.app.shared.GetCommunityWallDataAction.SearchType;
import com.ziplly.app.shared.GetCommunityWallDataResult;

public class GetCommunityWallDataActionHandler extends
    AbstractTweetActionHandler<GetCommunityWallDataAction, GetCommunityWallDataResult> {

  private HashtagDAO hashtagDao;
  private Logger logger = Logger.getLogger(GetCommunityWallDataAction.class.getName());
  private TweetBLI tweetBli;

  @Inject
  public GetCommunityWallDataActionHandler(Provider<EntityManager> entityManagerProvider,
      AccountDAO accountDao,
      SessionDAO sessionDao,
      TweetDAO tweetDao,
      AccountBLI accountBli,
      TweetBLI tweetBli,
      HashtagDAO hashtagDao) {
    super(entityManagerProvider, accountDao, sessionDao, tweetDao, accountBli);
    this.tweetBli = tweetBli;
    this.hashtagDao = hashtagDao;
  }

  @Override
  public GetCommunityWallDataResult doExecute(GetCommunityWallDataAction action,
      ExecutionContext arg1) throws DispatchException {

    if (action.getSearchType() != SearchType.TWEET_BY_ID) {
      validateSession();
    }

    if (action.getSearchType() == SearchType.CATEGORY) {
      return getTweetsByCategory(action);
    } else if (action.getSearchType() == SearchType.HASHTAG) {
      return getTweetsByHashtag(action);
    } else if (action.getSearchType() == SearchType.TWEET_BY_ID) {
      return getTweetById(action);
    } else if (action.getSearchType() == SearchType.NEIGHBORHOOD) {
      Preconditions.checkNotNull(action.getNeighborhood());
      return getTweetsByNeighborhood(action);
    }
    throw new IllegalArgumentException();
  }

  private GetCommunityWallDataResult
      getTweetsByNeighborhood(GetCommunityWallDataAction action) throws NotFoundException {

    GetCommunityWallDataResult result = new GetCommunityWallDataResult();
    List<TweetDTO> tweets =
        tweetDao.findTweetsByNeighborhood(
            action.getNeighborhood().getNeighborhoodId(),
            action.getPage(),
            action.getPageSize());
    result.setTweets(tweets);
    return result;
  }

  private GetCommunityWallDataResult
      getTweetById(GetCommunityWallDataAction action) throws NotFoundException {
    GetCommunityWallDataResult result = new GetCommunityWallDataResult();
    try {
      Long tweetId = Long.parseLong(action.getTweetId());
      TweetDTO tweet = tweetDao.findTweetById(tweetId);
      ArrayList<TweetDTO> tweets = new ArrayList<TweetDTO>();
      tweets.add(tweet);
      result.setTweets(tweets);
    } catch (NotFoundException nfe) {
      logger.severe(String.format("Couldn't find tweets for tweetId %s", action.getTweetId()));
      throw nfe;
    } catch (NumberFormatException nfe) {
      throw new IllegalArgumentException();
    }
    return result;
  }

  private GetCommunityWallDataResult
      getTweetsByHashtag(GetCommunityWallDataAction action) throws NotFoundException {
    Long neighborhoodId = session.getLocation().getNeighborhood().getNeighborhoodId();
    List<TweetDTO> tweets =
        hashtagDao.getTweetsForTagAndNeighborhood(
            action.getHashtag(),
            neighborhoodId,
            action.getPage(),
            action.getPageSize());

    GetCommunityWallDataResult gcwdr = new GetCommunityWallDataResult();
    gcwdr.setTweets(tweets);
    return gcwdr;
  }

  private GetCommunityWallDataResult
      getTweetsByCategory(GetCommunityWallDataAction action) throws NotFoundException {

    TweetType type = action.getType();
    List<TweetDTO> tweets = null;

    if (type.equals(TweetType.ALL)) {
      tweets =
          tweetDao.findTweetsByNeighborhood(
              action.getNeighborhood().getNeighborhoodId(),
              action.getPage(),
              action.getPageSize());
    } else if (type.equals(TweetType.COUPON)) {
      tweets = tweetDao.findCouponsByNeighborhood(
          action.getNeighborhood().getNeighborhoodId(), 
          action.getPage(), 
          action.getPageSize());
    } else {
      tweets =
          tweetDao.findTweetsByTypeAndNeighborhood(type, action
              .getNeighborhood()
              .getNeighborhoodId(), action.getPage(), action.getPageSize());
    }

    GetCommunityWallDataResult gcwdr = new GetCommunityWallDataResult();
    gcwdr.setTweets(tweets);
    return gcwdr;
  }

  @Override
  public Class<GetCommunityWallDataAction> getActionType() {
    return GetCommunityWallDataAction.class;
  }
}
