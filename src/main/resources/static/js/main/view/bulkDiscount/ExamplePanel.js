Ext.define('Main.view.bulkDiscount.ExamplePanel', {
    extend: 'Ext.tab.Panel',
    xtype: 'basic-tabs',

    width: 500,
    height: 300,
    defaults: {
        bodyPadding: 10,
        scrollable: true
    },
    tabBar: {
        layout: {
            pack: 'center'
        }
    },
    // items: [{
    //     title: 'Active Tab',
    //     html: '123'
    // }, {
    //     title: 'Inactive Tab',
    //     html: '234'
    // }]
});