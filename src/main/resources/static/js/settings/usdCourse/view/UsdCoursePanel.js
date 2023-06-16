Ext.define('UsdCourse.view.UsdCoursePanel', {
    xtype: 'usdcoursepanel',
    extend: 'Ext.form.Panel',
    alias: 'widget.UsdCourse-panel',
    title: 'Курс USD',
    header: {
        titleAlign: 'center'
    },
    region: 'center',
    scrollable: true,
    layout: {
        type: 'fit'
    },
    items: [
        {
            xtype: 'form',
            id: 'coursesForm',
            bodyStyle: 'padding:10px 40px 0px 20px',
            scrollable: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            flex: 1,
            listeners: {
                afterrender: function (form) {
                    let me = form;
                    Ext.Ajax.request({
                        url: '/settings/getUsdCourses',
                        method: 'GET',
                        success: function (rs) {
                            let response = Ext.JSON.decode(rs.responseText)
                            let data = response.data

                            for (let fiatCurrency of data) {
                                let fiatCurrencyItems = []
                                for (let dealType of fiatCurrency.dealTypes) {
                                    let dealTypesItems = []
                                    for (let cryptoCurrency of dealType.cryptoCurrencies) {
                                        let cryptoCurrencyInput = {
                                            xtype: 'numberfield',
                                            decimalSeparator: '.',
                                            step: 0.1,
                                            name: (fiatCurrency.name + "." + dealType.name + "."
                                                + cryptoCurrency.name).toLowerCase(),
                                            fieldLabel: cryptoCurrency.name,
                                            value: cryptoCurrency.value,
                                            defaultValue: cryptoCurrency.value,
                                            layout: 'anchor',
                                            msgTarget: 'side',
                                            emptyText: 'Введите значение.',
                                            allowBlank: false,
                                            listeners: {
                                                change: function(me) {
                                                    if (me.value !== me.defaultValue) me.setFieldStyle('color:#157fcc; font-weight: bold;');
                                                    else me.setFieldStyle('color:black; font-weight: normal;')
                                                }
                                            }
                                        }
                                        dealTypesItems.push(cryptoCurrencyInput)
                                    }
                                    let dealTypeFieldSet = {
                                        xtype: 'fieldset',
                                        title: dealType.displayName,
                                        collapsible: true,
                                        layout: {
                                            type: 'vbox',
                                            align: 'stretch'
                                        },
                                        defaults: {
                                            labelWidth: 90,
                                        },
                                        items: dealTypesItems
                                    }
                                    fiatCurrencyItems.push(dealTypeFieldSet)
                                }

                                let fiatCurrencyFieldSet = {
                                    xtype: 'fieldset',
                                    title: fiatCurrency.displayName,
                                    collapsible: true,
                                    layout: 'anchor',
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
            buttonAlign: 'center',
            buttons: [
                {
                  text: 'Восстановить значения',
                  handler: function () {
                      console.log('123');
                  }
                },
                {
                    text: 'Сохранить',
                    handler: function () {
                        let values = Ext.ComponentQuery.query('[id=coursesForm]')[0].getValues()
                        let courses = []
                        for (let key of Object.keys(values)) {
                            courses.push({
                                key: key,
                                value: values[key]
                            })
                        }

                    }
                }
            ]
        }
    ]
});
