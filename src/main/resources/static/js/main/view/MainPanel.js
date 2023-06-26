Ext.define('Main.view.MainPanel', {
    xtype: 'mainpanel',
    extend: 'Ext.form.Panel',
    id: 'mainPanel',
    controller: 'mainController',
    requires: [
        'Main.view.usdCourse.UsdCoursePanel',
        'Main.view.usdCourse.UsdCourseController',
    ],
    header: false,
    scrollable: true,
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'panel',
            header: false,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            dockedItems: [
                {
                    xtype: 'toolbar',
                    dock: 'left',
                    id: 'mainToolBar',
                    padding: '0 0 0 0',
                    items: [
                        {
                            xtype: 'button',
                            iconCls: 'fas fa-square-root-alt',
                            menu: [
                                {
                                    text: 'Курс доллара',
                                    iconCls: 'fas fa-dollar-sign',
                                    handler: 'usdCourseClick'
                                }
                            ]
                        }
                    ]
                },
            ],
            items: [
                {
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    height: 40,
                    items: [
                        {
                            xtype: 'container',
                            flex: 0.33,
                            style: {
                                backgroundColor: '#5fa2dd',
                            },
                            items: [
                                {
                                    xtype: 'button',
                                    iconCls: 'fas fa-bars',
                                    handler: 'collapse',
                                    width: 40,
                                    height: 40
                                },
                            ]
                        },
                        {
                            xtype: 'container',
                            flex: 0.33,
                            layout: {
                                type: 'vbox',
                                align: 'center',
                                pack: 'center'
                            },
                            cls: 'main-panel-header-title',
                            items: [
                                {
                                    xtype: 'container',
                                    html: 'Админ панель',
                                }
                            ]
                        },
                        {
                            xtype: 'container',
                            style: {
                                backgroundColor: '#5fa2dd',
                            },
                            html: '',
                            flex: 0.33
                        }
                    ]

                },
                {
                    xtype: 'panel',
                    padding: '10 0 0 0',
                    id: 'mainFramePanel',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    scrollable: true,
                    items: [
                        {
                            xtype: 'usdcoursepanel'
                        }
                    ]
                }
            ]
        }
    ]
});
