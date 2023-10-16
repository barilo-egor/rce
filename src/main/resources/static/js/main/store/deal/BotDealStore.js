Ext.define('Main.store.deal.BotDealStore', {
    extend: 'Ext.data.Store',
    storeId: 'botDealStore',
    autoLoad: false,
    pageSize: 15,
    fields: ['pid', 'dealStatus', 'chatId'],
    proxy: {
        type: 'ajax',
        url: '/web/deal/bot/findAll',
        reader: {
            type: 'json',
            rootProperty: 'items'
        }
    }
})