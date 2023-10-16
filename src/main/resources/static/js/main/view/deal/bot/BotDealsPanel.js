Ext.define('Main.view.deal.bot.BotDealsPanel', {
    extend: 'Main.view.components.FramePanel',
    xtype: 'botdealspanel',
    title: {
        xtype: 'mainframetitle',
        text: 'Сделки из бота'
    },
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'grid',
            padding: '0 20 0 20',
            store: 'botDealStore',
            listeners: {
                afterrender: function (me) {
                    me.getStore().load()
                }
            },
            dockedItems: [
                {
                    xtype: 'pagingtoolbar',
                    store: 'botDealStore',
                    displayInfo: true
                }
            ],
            emptyText: 'Сделки отсутствуют',
            columns: [
                {
                    text: 'Номер',
                    dataIndex: 'pid',
                    width: 100
                },
                {
                    text: 'Статус',
                    dataIndex: 'dealStatus',
                    flex: 1
                },
                {
                    text: 'Chat id',
                    dataIndex: 'chatId',
                    width: 100
                },
                {
                    xtype: 'actioncolumn',
                    width: 30,
                    items: [
                        {
                            iconCls: 'far fa-arrow-alt-circle-right'
                        }
                    ]
                }
            ]
        }
    ]
})