Ext.define('Main.view.usdCourse.UsdCoursePanel', {
    xtype: 'usdcoursepanel',
    extend: 'Main.view.components.FramePanel',
    controller: 'usdCourseController',
    title: {
        xtype: 'mainframetitle',
        text: 'Курс USD'
    },
    requires: [
        'Main.view.components.panel.DiscountHintPanel'
    ],
    padding: '0 0 0 0',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'form',
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
                    defaults: {
                        collapsible: false,
                        layout: {
                            type: 'vbox',
                            align: 'stretch'
                        }
                    },
                    items: [
                        {
                            xtype: 'fieldset',
                            id: 'cryptoCourses',
                            title: 'Крипто курсы',
                            flex: 0.35
                        },
                        {
                            xtype: 'fieldset',
                            id: 'discountsFieldSet',
                            title: 'Скидки',
                            flex: 0.65,
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
                                    validator: ValidatorUtil.validateDiscount
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
                                    validator: ValidatorUtil.validateDiscount
                                },
                                {
                                    xtype: 'discounthintpanel'
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
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            buttonAlign: 'center',
            buttons: [
                {
                    text: 'Сохранить',
                    iconCls: 'fas fa-save greenBtn',
                    cls: 'greenBtn',
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
