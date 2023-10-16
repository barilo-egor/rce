Ext.define('Main.view.deal.bot.BotDealsPanel', {
    extend: 'Ext.panel.Panel',
    xtype: 'botdealspanel',
    title: 'Сделки из бота',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'grid',
            columns: [
                {
                    text: 'Статус'
                },
                {
                    text: 'Номер'
                },
                {
                    text: 'Chat id'
                },
                {
                    text: 'username'
                },
                {
                    xtype: 'actioncolumn',
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