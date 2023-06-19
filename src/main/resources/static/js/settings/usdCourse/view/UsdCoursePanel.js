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
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'form',
            id: 'coursesForm',
            scrollable: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
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
                                            flex: 1,
                                            msgTarget: 'side',
                                            emptyText: 'Введите значение.',
                                            // hideTrigger: true,
                                            allowBlank: false,
                                            triggers: {
                                                reset: {
                                                    tooltip: 'Вернуть значение',
                                                    cls: 'x-form-clear-trigger',
                                                    hidden: true,
                                                    handler: function (me) {
                                                        me.setValue(me.defaultValue)
                                                    }
                                                }
                                            },
                                            listeners: {
                                                beforerender: function (me) {
                                                    me.getTriggers().spinner.hide();
                                                },
                                                change: function (me) {
                                                    if (me.value !== me.defaultValue) {
                                                        me.getTriggers().reset.show()
                                                        me.setFieldStyle('color:#157fcc; font-weight: bold;')
                                                    } else {
                                                        me.getTriggers().reset.hide()
                                                        me.setFieldStyle('color: #404040;\n' +
                                                            'padding: 5px 10px 4px;\n' +
                                                            'background-color: #fff;\n' +
                                                            'font: 300 13px/21px \'Open Sans\', \'Helvetica Neue\', helvetica, arial, verdana, sans-serif;\n' +
                                                            'min-height: 30px;')
                                                    }
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
                    iconCls: 'fa-solid fa-rotate-right',
                    handler: function () {
                        let fiatCurrencies = Ext.ComponentQuery.query('[id=coursesForm]')[0].items.items
                        for (let fiatCurrency of fiatCurrencies) {
                            let dealTypes = fiatCurrency.items.items
                            for (let dealType of dealTypes) {
                                let cryptoCurrencies = dealType.items.items
                                for (let cryptoCurrency of cryptoCurrencies) {
                                    cryptoCurrency.setValue(cryptoCurrency.defaultValue)
                                }
                            }
                        }
                    }
                },
                {
                    text: 'Сохранить',
                    iconCls: 'fa-regular fa-floppy-disk',
                    cls: 'saveBtn',
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
