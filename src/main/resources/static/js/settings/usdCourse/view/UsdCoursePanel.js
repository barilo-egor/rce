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
                                            xtype: 'container',
                                            layout: {
                                                type: 'hbox',
                                                align: 'stretch'
                                            },
                                            padding: '0 0 5 0',
                                            items: [
                                                {
                                                    xtype: 'numberfield',
                                                    decimalSeparator: '.',
                                                    step: 0.1,
                                                    name: (fiatCurrency.name + "." + dealType.name + "."
                                                        + cryptoCurrency.name).toLowerCase(),
                                                    data: {
                                                        fiatCurrency: fiatCurrency.name,
                                                        dealType: dealType.name,
                                                        cryptoCurrency: cryptoCurrency.name
                                                    },
                                                    fieldLabel: cryptoCurrency.name,
                                                    value: cryptoCurrency.value,
                                                    defaultValue: cryptoCurrency.value,
                                                    flex: 0.55,
                                                    padding: '0 2 0 0',
                                                    msgTarget: 'side',
                                                    emptyText: 'Введите значение.',
                                                    allowBlank: false,
                                                    hideTrigger: true,
                                                    listeners: {
                                                        change: function (me) {
                                                            let button = me.up('container').items.items[1]
                                                            button.setDisabled(false)
                                                            if (me.value !== me.defaultValue) {
                                                                button.setDisabled(false)
                                                                me.setFieldStyle('color:#157fcc; font-weight: bold;')
                                                            } else {
                                                                button.setDisabled(true)
                                                                me.setFieldStyle('color: #404040;\n' +
                                                                    'padding: 5px 10px 4px;\n' +
                                                                    'background-color: #fff;\n' +
                                                                    'font: 300 13px/21px \'Open Sans\', \'Helvetica Neue\', helvetica, arial, verdana, sans-serif;\n' +
                                                                    'min-height: 30px;')
                                                            }
                                                        }
                                                    }
                                                },
                                                {
                                                    xtype: 'button',
                                                    flex: 0.05,
                                                    tooltip: 'Восстановить значение',
                                                    iconCls: 'fa-solid fa-rotate-right',
                                                    disabled: true,
                                                    cls: 'returnValueBtn',
                                                    handler: function (btn) {
                                                        let input = btn.up('container').items.items[0]
                                                        input.setValue(input.defaultValue)
                                                    }
                                                },
                                                {
                                                    xtype: 'numberfield',
                                                    decimalSeparator: '.',
                                                    decimalPrecision: 8,
                                                    value: cryptoCurrency.defaultCheckValue,
                                                    hideTrigger: true,
                                                    flex: 0.15,
                                                    padding: '0 2 0 2',
                                                    listeners: {
                                                        change: function (me) {
                                                            let container = me.up('container')
                                                            let usdCourseField = container.items.items[0]
                                                            let usdCourse = usdCourseField.value
                                                            let resultInput = container.items.items[3]
                                                            let params = {
                                                                cryptoAmount: me.value,
                                                                usdCourse: usdCourse,
                                                                fiatCurrency: usdCourseField.data.fiatCurrency,
                                                                cryptoCurrency: usdCourseField.data.cryptoCurrency,
                                                                dealType: usdCourseField.data.dealType
                                                            }
                                                            Ext.Ajax.request({
                                                                url: '',
                                                                method: 'GET',
                                                                params: params,
                                                                success: function (response) {

                                                                }
                                                            })
                                                        }
                                                    }
                                                },
                                                {
                                                    xtype: 'numberfield',
                                                    editable: false,
                                                    decimalSeparator: '.',
                                                    decimalPrecision: 8,
                                                    hideTrigger: true,
                                                    flex: 0.25,
                                                    padding: '0 2 0 0',
                                                }
                                            ]
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
                },
                {
                    text: 'Восстановить значения',
                    iconCls: 'fa-solid fa-rotate-right',
                    cls: 'returnValuesBtn',
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
                }
            ]
        }
    ]
});
