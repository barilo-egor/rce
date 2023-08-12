Ext.define('Main.view.bulkDiscount.store.BulkDiscountStore', {
    extend: 'Ext.data.Store',
    model: 'Main.view.bulkDiscount.model.BulkDiscountModel',
    pageSize: 100,
    proxy: {
        type: 'memory',
        enablePaging: true,
        reader: {
            type: 'json'
        }
    }
});