Ext.define('Main.view.usdCourse.UsdCoursePanel', {
    xtype: 'usdcoursepanel',
    extend: 'Main.view.components.FramePanel',
    controller: 'usdCourseController',
    title: {
        xtype: 'mainframetitle',
        text: 'Курс USD'
    },
    scrollable: true,
    padding: '0 0 0 0',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'form',
            scrollable: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'fieldset',
                    title: 'Расчетные данные',
                    collapsible: true,
                    collapsed: true,
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'fieldset',
                            id: 'cryptoCourses',
                            collapsible: false,
                            title: 'Крипто курсы',
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            flex: 0.35
                        },
                        {
                            xtype: 'fieldset',
                            id: 'discountsFieldSet',
                            collapsible: false,
                            title: 'Скидки',
                            flex: 0.65,
                            layout: {
                                type: 'vbox',
                                align: 'stretch'
                            },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    boxLabel: 'Учитывать скидки',
                                    listeners: {
                                        change: 'turnDiscounts'
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    fieldLabel: "Персональная скидка",
                                    value: 0,
                                    decimalSeparator: '.',
                                    padding: '0 0 2 0',
                                    disabled: true,
                                    hideTrigger: true,
                                    listeners: {
                                        change: 'updateResultAmounts'
                                    },
                                    msgTarget: 'side',
                                    validator: function (val) {
                                        if (!val) return 'Введите значение.'
                                        if (val < -99 || val > 99) {
                                            return 'Значение должно быть >-99 и <99.'
                                        } else return true
                                    }
                                },
                                {
                                    xtype: 'numberfield',
                                    fieldLabel: "Оптовая скидка",
                                    value: 0,
                                    decimalSeparator: '.',
                                    padding: '0 0 2 0',
                                    disabled: true,
                                    hideTrigger: true,
                                    listeners: {
                                        change: 'updateResultAmounts'
                                    },
                                    msgTarget: 'side',
                                    validator: function (val) {
                                        if (!val) return 'Введите значение.'
                                        if (val < -99 || val > 99) {
                                            return 'Значение должно быть >-99 и <99.'
                                        } else return true
                                    }
                                },
                                {
                                    xtype: 'panel',
                                    frame: true,
                                    hidden: true,
                                    padding: '5 5 5 5',
                                    style: {
                                        borderColor: '#919191',
                                        borderWidth: '1px'
                                    },
                                    html: 'Введите положительное значение для скидки, либо отрицательное для надбавки.'
                                }
                            ]
                        },
                    ]
                }
            ],
            listeners: {
                afterrender: 'cryptoCoursesAfterRender'
            }
        },
        {
            xtype: 'form',
            id: 'coursesForm',
            scrollable: true,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            buttonAlign: 'center',
            buttons: [
                {
                    text: 'Сохранить',
                    iconCls: 'fas fa-save saveBtn',
                    cls: 'saveBtn',
                    handler: 'onSaveClick'
                },
                {
                    text: 'Восстановить значения',
                    iconCls: 'fas fa-redo blueButton',
                    cls: 'blueButton',
                    handler: 'returnValues'
                }
            ]
        }
    ]
});
