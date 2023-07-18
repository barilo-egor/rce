Ext.define('Main.store.enum.FiatCurrenciesStore', {
    extend: 'Ext.data.Store',
    alias: 'store.fiatCurrenciesStore',
    storeId: 'fiatCurrenciesStore',
    model: 'Main.model.enum.FiatCurrency',
    autoLoad: true,
    proxy: {
        type: 'ajax',
        url: '/web/enum/fiatCurrencies',
        reader: {
            type: 'json'
        }
    }
});