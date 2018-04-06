/**
 * @file  SampleDeviceListViewController.h
 * @brief CameraRemoteSampleApp
 *
 * Copyright 2014 Sony Corporation
 */

#import "SampleDeviceDiscovery.h"

@interface SampleDeviceListViewController
    : UIViewController <UITableViewDelegate, UITableViewDataSource,
                        SampleDeviceDiscoveryDelegate>
@property (weak, nonatomic) IBOutlet UIButton *discoveryOutlet;
- (IBAction)discoveryButton:(id)sender;
@property (weak, nonatomic) IBOutlet UITableView *deviceListOutlet;

@end
