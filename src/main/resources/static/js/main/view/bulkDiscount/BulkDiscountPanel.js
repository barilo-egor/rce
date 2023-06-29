Ext.define('Main.view.bulkDiscount.BulkDiscountPanel', {
    xtype: 'bulkdiscountpanel',
    extend: 'Main.view.components.FramePanel',
    controller: 'bulkDiscountController',
    title: {
        xtype: 'mainframetitle',
        text: 'Оптовые скидки'
    },
    // layout: {
    //     type: 'vbox',
    //     align: 'stretch'
    // },
    scrollable: true,
    items: [
        {
            xtype: 'tabpanel',
            id: 'discountsTabPanel',
            listeners: {
                afterrender: 'bulkDiscountsAfterRender',
            }
        }
    ]
});