Ext.define('Main.view.bulkDiscount.model.BulkDiscountModel', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'sum', type: 'number'},
        {name: 'percent', type: 'number'},
        {name: 'fiatCurrency', type: 'string'},
        {name: 'dealType', type: 'string'}
    ],
});