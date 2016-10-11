package com.applikey.mattermost.mvp.presenters;

import com.applikey.mattermost.App;
import com.applikey.mattermost.models.post.Post;
import com.applikey.mattermost.models.post.PostResponse;
import com.applikey.mattermost.mvp.views.ChatView;
import com.applikey.mattermost.storage.db.PostStorage;
import com.applikey.mattermost.storage.db.TeamStorage;
import com.applikey.mattermost.web.Api;
import com.applikey.mattermost.web.ErrorHandler;
import com.arellomobile.mvp.InjectViewState;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@InjectViewState
public class ChatPresenter extends BasePresenter<ChatView> {

    private static final int PAGE_SIZE = 60;

    @Inject
    PostStorage mPostStorage;

    @Inject
    TeamStorage mTeamStorage;

    @Inject
    Api mApi;

    public ChatPresenter() {
        App.getComponent().inject(this);
    }

    public void getInitialData(String channelId) {
        final ChatView view = getViewState();
        mPostStorage.listByChannel(channelId)
                .subscribe(view::displayData,
                        view::onFailure);
    }

    public void fetchData(String channelId, int offset) {
        mSubscription.add(mTeamStorage.getChosenTeam()
                .flatMap(team -> mApi.getPostsPage(team.getId(), channelId, offset, PAGE_SIZE)
                        .subscribeOn(Schedulers.io())
                        .doOnError(ErrorHandler::handleError))
                .map(response -> transform(response, offset))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> {
                    mPostStorage.saveAll(posts);
                    getViewState().onDataFetched();
                }, ErrorHandler::handleError));
    }

    private List<Post> transform(PostResponse response, int offset) {
        final List<String> order = response.getOrder();

        for (int i = 0; i < order.size(); i++) {
            final String id = order.get(i);

            response.getPosts().get(id).setPriority(i + offset);
        }

        return new ArrayList<>(response.getPosts().values());
    }
}
