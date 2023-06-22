Ext.define('UsdCourse.view.UsdCourseController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.usdCourseController',

    calculate: function (me) {
        let cryptoCoursesFieldSet = Ext.ComponentQuery.query('[id=cryptoCourses]')[0]
        let cryptoCoursesInputs = cryptoCoursesFieldSet.items.items
        let container = me.up('container')
        let cryptoAmount = container.items.items[2].value
        if (!cryptoAmount || cryptoAmount === '0' || cryptoAmount === 0) return;
        let usdCourseField = container.items.items[0]
        if (!usdCourseField.value || usdCourseField.value === '0' || usdCourseField.value === 0) return;
        let cryptoCourse
        for (let cryptoCourseInput of cryptoCoursesInputs) {
            if (cryptoCourseInput.xtype !== 'numberfield') return;
            if (cryptoCourseInput.fieldLabel === usdCourseField.cryptoCurrency) {
                cryptoCourse = cryptoCourseInput.value
                break
            }
        }
        let resultInput = container.items.items[3]
        let usdCourse = usdCourseField.value
        let discountsFieldSetItems = Ext.ComponentQuery.query('[id=discountsFieldSet]')[0].items.items
        let personalDiscount = null
        let bulkDiscount = null
        if (discountsFieldSetItems[0].value) {
            let personalValue = discountsFieldSetItems[1].value
            let bulkValue = discountsFieldSetItems[2].value
            if (personalValue > 99 || personalValue < -99) return;
            if (bulkValue > 99 || bulkValue < -99) return;
            personalDiscount = personalValue
            bulkDiscount = bulkValue
        } else {
            personalDiscount = 0
            bulkDiscount = 0
        }
        let params = {
            cryptoAmount: cryptoAmount,
            usdCourse: usdCourse,
            fiatCurrency: usdCourseField.fiatCurrency,
            cryptoCurrency: usdCourseField.cryptoCurrency,
            dealType: usdCourseField.dealType,
            cryptoCourse: cryptoCourse,
            personalDiscount: personalDiscount,
            bulkDiscount: bulkDiscount
        }
        Ext.Ajax.request({
            url: '/settings/calculate',
            method: 'GET',
            params: params,
            success: function (rs) {
                let response = Ext.JSON.decode(rs.responseText)
                resultInput.setValue(response.amount)
                resultInput.setLoading(false)
            }
        })
    },

    usdCourseChange: function (me) {
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
        this.calculate(me)
    },

    returnValue: function (btn) {
        let input = btn.up('container').items.items[0]
        input.setValue(input.defaultValue)
        let cryptoAmountField = btn.up('fieldset').items.items[3]
        cryptoAmountField.fireEvent('valuechanged')
        this.calculate(btn)
    },

    returnValues: function () {
        let fiatCurrencies = Ext.ComponentQuery.query('[id=coursesForm]')[0].items.items
        for (let fiatCurrency of fiatCurrencies) {
            let dealTypes = fiatCurrency.items.items
            for (let dealType of dealTypes) {
                let containers = dealType.items.items
                for (let container of containers) {
                    let courseField = container.items.items[0]
                    courseField.setValue(courseField.defaultValue)
                }
            }
        }
    },

    updateCourses: function (component) {
        let me = this
        component.up('fieldset').setLoading('Загрузка')
        Ext.Ajax.request({
            url: '/settings/cryptoCourses',
            method: 'GET',
            success: function (rs) {
                let response = Ext.JSON.decode(rs.responseText)
                let currencies = response.currencies
                let cryptoCoursesFieldSetItems = Ext.ComponentQuery.query('[id=cryptoCourses]')[0].items.items

                for (let cryptoCurrency of currencies) {
                    for (let item of cryptoCoursesFieldSetItems) {
                        if (item.fieldLabel === cryptoCurrency.name) {
                            item.setValue(cryptoCurrency.currency)
                            break
                        }
                    }
                }
                me.updateResultAmounts()
                component.up('fieldset').setLoading(false)
            }
        })
    },

    cryptoCoursesAfterRender: function (me) {
        Ext.Ajax.request({
            url: '/settings/cryptoCourses',
            method: 'GET',
            async: false,
            success: function (rs) {
                let response = Ext.JSON.decode(rs.responseText)
                let currencies = response.currencies
                let cryptoCoursesFieldSet = Ext.ComponentQuery.query('[id=cryptoCourses]')[0]

                for (let cryptoCurrency of currencies) {
                    cryptoCoursesFieldSet.insert({
                        xtype: 'numberfield',
                        fieldLabel: cryptoCurrency.name,
                        value: cryptoCurrency.currency,
                        decimalSeparator: '.',
                        padding: '0 0 2 0',
                        editable: false,
                        hideTrigger: true
                    })
                }
                cryptoCoursesFieldSet.insert({
                    xtype: 'container',
                    layout: {
                        type: 'vbox',
                        align: 'center'
                    },
                    items: [
                        {
                            xtype: 'button',
                            text: 'Обновить курсы',
                            handler: 'updateCourses',
                            cls: 'blueButton',
                            iconCls: 'fa-solid fa-rotate-right',
                        }
                    ]
                })
            }
        })

        let coursesForm = Ext.ComponentQuery.query('[id=coursesForm]')[0];
        Ext.Ajax.request({
            url: '/settings/getUsdCourses',
            method: 'GET',
            async: false,
            success: function (rs) {
                let response = Ext.JSON.decode(rs.responseText)
                let data = response.data

                for (let fiatCurrency of data) {
                    let fiatCurrencyItems = []
                    for (let dealType of fiatCurrency.dealTypes) {
                        let dealTypesItems = []
                        let i = 0;
                        for (let cryptoCurrency of dealType.cryptoCurrencies) {
                            let cryptoCurrencyInput = {
                                xtype: 'container',
                                controller: 'usdCourseController',
                                layout: {
                                    type: 'hbox',
                                    align: 'stretch'
                                },
                                defaults: {
                                    labelWidth: 70
                                },
                                padding: '0 0 5 0',
                                items: [
                                    {
                                        xtype: 'numberfield',
                                        decimalSeparator: '.',
                                        step: 0.1,
                                        name: (fiatCurrency.name + "." + dealType.name + "."
                                            + cryptoCurrency.name).toLowerCase(),
                                        fiatCurrency: fiatCurrency.name,
                                        dealType: dealType.name,
                                        cryptoCurrency: cryptoCurrency.name,
                                        fieldLabel: cryptoCurrency.name,
                                        value: cryptoCurrency.value,
                                        defaultValue: cryptoCurrency.value,
                                        flex: 0.5,
                                        padding: '0 2 0 0',
                                        msgTarget: 'side',
                                        allowBlank: false,
                                        hideTrigger: true,
                                        listeners: {
                                            change: 'usdCourseChange'
                                        },
                                        validator: function (value) {
                                            if (!value) return 'Введите значение.'
                                            if (value === 0 || value === '0' || value < 0) return 'Введите значение больше 0.'
                                            return true
                                        }
                                    },
                                    {
                                        xtype: 'button',
                                        weight: 50,
                                        tooltip: 'Восстановить значение',
                                        iconCls: 'fa-solid fa-xmark',
                                        disabled: true,
                                        cls: 'returnValueBtn',
                                        handler: 'returnValue'
                                    },
                                    {
                                        xtype: 'numberfield',
                                        decimalSeparator: '.',
                                        flex: 0.25,
                                        decimalPrecision: 8,
                                        value: cryptoCurrency.defaultCheckValue,
                                        hideTrigger: true,
                                        padding: '0 2 0 2',
                                        listeners: {
                                            afterrender: 'calculate',
                                            change: 'calculate',
                                        },
                                        validator: function (value) {
                                            if (!value) return 'Введите значение.'
                                            if (value === 0 || value === '0' || value < 0) return 'Введите значение больше 0.'
                                            return true
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
                            i++
                        }
                        let dealTypeFieldSet = {
                            xtype: 'fieldset',
                            title: dealType.displayName,
                            collapsible: false,
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
                    coursesForm.insert(fiatCurrencyFieldSet)
                }
            }
        })
    },

    turnDiscounts: function (me, newValue) {
        let fieldSetItems = me.up('fieldset').items.items
        fieldSetItems[1].setDisabled(!newValue)
        fieldSetItems[2].setDisabled(!newValue)
        if (newValue) fieldSetItems[3].show()
        else fieldSetItems[3].hide()
        this.updateResultAmounts()
    },

    updateResultAmounts: function () {
        let fiatCurrencies = Ext.ComponentQuery.query('[id=coursesForm]')[0].items.items
        for (let fiatCurrency of fiatCurrencies) {
            let dealTypes = fiatCurrency.items.items
            for (let dealType of dealTypes) {
                let containers = dealType.items.items
                for (let container of containers) {
                    let resultAmountField = container.items.items[3]
                    this.calculate(resultAmountField)
                }
            }
        }
    },

    onSaveClick: function () {
        let courses = []
        let values = []
        let coursesFormItems = Ext.ComponentQuery.query('[id=coursesForm]')[0].items.items
        coursesFormItems.forEach(
            coursesFormItem => coursesFormItem.items.items.forEach(
                dealTypeItem => dealTypeItem.items.items.forEach(
                    containerItem => values.push(containerItem.items.items[0]))
            )
        )
        for (let value of values) {
            let courseObj = {}
            let name = value.name
            let keys = name.split('.')
            keys.map(key => key.toUpperCase())
            courseObj.fiatCurrency = keys[0]
            courseObj.dealType = keys[1]
            courseObj.cryptoCurrency = keys[2]
            courseObj.value = value.getValue()
            courses.push(courseObj)
        }
        let jsonData = {
            courses: JSON.stringify(courses.map(function (el) {
                return { name: el };
            }))
        }
        Ext.Ajax.request({
            url: '/settings/saveUsdCourses',
            method: 'POST',
            jsonData: jsonData,
            success: function (rs) {
                alert('ok')
            }
        })
    }
})