package tgb.btc.rce.sender;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tgb.btc.library.interfaces.service.bean.bot.user.IModifyUserService;
import tgb.btc.library.interfaces.service.bean.bot.user.IReadUserService;
import tgb.btc.rce.service.IMessageImageService;
import tgb.btc.rce.service.util.IMenuService;

@ExtendWith(MockitoExtension.class)
class MessageImageResponseSenderTest {

    @Mock
    private IMessageImageService messageImageService;

    @Mock
    private IResponseSender responseSender;

    @Mock
    private IModifyUserService modifyUserService;

    @Mock
    private IMenuService menuService;

    @Mock
    private IReadUserService readUserService;

    @InjectMocks
    private MessageImageResponseSender messageImageResponseSender;



}