Ext.define('UsdCourse.view.UsdCoursePanel', {
    xtype: 'usdcoursepanel',
    extend: 'Ext.form.Panel',
    alias: 'widget.UsdCourse-panel',
    title: 'Замена курса USD',
    header: {
        titleAlign: 'center'
    },
    bodyStyle: 'padding:5px 5px',
    region: 'center',
    layout: {
        type: 'vbox',
        align: 'center'
    },
    items: [
        {
            xtype: 'form',
            id: 'coursesForm',
            listeners: {
                afterrender: function (form) {
                    let me = form;
                    console.log('qwe')
                    Ext.Ajax.request({
                        url: '/settings/getUsdCourses',
                        method: 'GET',
                        success: function (rs) {
                            let response = Ext.JSON.decode(rs.responseText)
                            let items = []
                            let data = response.data

                            for (let fiatCurrency of data) {
                                let fiatCurrencyItems = []
                                for (let dealType of fiatCurrency.dealTypes) {
                                    let dealTypesItems = []
                                    for (let cryptoCurrency of dealType.cryptoCurrencies) {
                                        let cryptoCurrencyInput = {
                                            xtype: 'textfield',
                                            name: cryptoCurrency.name,
                                            fieldLabel: cryptoCurrency.name,
                                            value: cryptoCurrency.value,
                                            msgTarget: 'side',
                                            allowBlank: false
                                        }
                                        dealTypesItems.push(cryptoCurrencyInput)
                                    }
                                    let dealTypeFieldSet = {
                                        xtype: 'fieldset',
                                        title: dealType.name,
                                        collapsible: true,
                                        layout: 'vbox',
                                        defaults: {
                                            labelWidth: 90,
                                        },
                                        items: dealTypesItems
                                    }
                                    fiatCurrencyItems.push(dealTypeFieldSet)
                                }

                                let fiatCurrencyFieldSet = {
                                    xtype: 'fieldset',
                                    title: fiatCurrency.name,
                                    collapsible: true,
                                    defaults: {
                                        labelWidth: 90,
                                        anchor: '100%',
                                        layout: 'hbox'
                                    },
                                    items: fiatCurrencyItems
                                }
                                me.insert(fiatCurrencyFieldSet)
                            }
                        }
                    })
                }
            },
            buttons: [
                {
                    text: 'Сохранить',
                    handler: function () {
                        // действие отправки
                    }
                }
            ]
        }
    ]
});
