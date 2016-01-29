package victoriaslmn.android.viper.sample;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Notification;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;
import rx.schedulers.TestScheduler;
import victoriaslmn.android.viper.sample.domain.contacts.Contact;
import victoriaslmn.android.viper.sample.domain.messages.GetChatsInteractor;
import victoriaslmn.android.viper.sample.domain.messages.Message;
import victoriaslmn.android.viper.sample.domain.messages.MessagesDataProvider;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class GetLastMessagesByContactsInteractorTest {

    @Test
    public void testLatestMessageByContactsIsCorrect() {
        Contact contact1 = new Contact(1, "contact1");
        Contact contact2 = new Contact(2, "contact2");
        Message message1 = new Message(1, contact1, "content", 10);
        Message message2 = new Message(2, contact1, "content", 20);
        Message message3 = new Message(3, contact2, "content", 30);
        MessagesDataProvider messagesDataProvider = mock(MessagesDataProvider.class);
        TestScheduler testScheduler = Schedulers.test();
        when(messagesDataProvider.getAllMessages(testScheduler))
                .thenReturn(Observable.just(Notification.createOnNext(Arrays.asList(message1, message2, message3))));
        GetChatsInteractor chatsInteractor
                = new GetChatsInteractor(testScheduler, testScheduler, messagesDataProvider);
        TestSubscriber<List<Message>> testSubscriber = new TestSubscriber<>();

        chatsInteractor.execute(testSubscriber);
        testScheduler.triggerActions();

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(Collections.singletonList(Arrays.asList(message3, message2)));
        verify(messagesDataProvider, times(1)).getAllMessages(testScheduler);
    }
}