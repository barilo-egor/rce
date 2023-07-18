Ext.define('Main.view.components.panel.DiscountHintPanel', {
    extend: 'Ext.panel.Panel',
    xtype: 'discounthintpanel',
    id: 'discountHintPanel',
    frame: true,
    hidden: true,
    padding: '5 5 5 5',
    style: {
        borderColor: '#919191',
        borderWidth: '1px',
        textAlign: 'center'
    },
    html: HtmlConstants.discountInfo
})