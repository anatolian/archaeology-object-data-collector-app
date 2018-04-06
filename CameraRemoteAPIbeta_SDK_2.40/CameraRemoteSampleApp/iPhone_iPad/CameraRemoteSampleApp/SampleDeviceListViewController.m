/**
 * @file  SampleDeviceListViewController.m
 * @brief CameraRemoteSampleApp
 *
 * Copyright 2014 Sony Corporation
 */

#import "SampleDeviceListViewController.h"
#import "SampleDeviceDiscovery.h"
#import "DeviceInfo.h"
#import "DeviceList.h"

@interface SampleDeviceListViewController () {
}
@end

@implementation SampleDeviceListViewController {
}

@synthesize discoveryOutlet;
@synthesize deviceListOutlet;

- (void)viewDidLoad
{
    [super viewDidLoad];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = FALSE;
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [discoveryOutlet
        setTitle:NSLocalizedString(@"DD_TEXT_START", @"DD_TEXT_START")
        forState:UIControlStateNormal];
    [discoveryOutlet setEnabled:YES];
    [deviceListOutlet setDelegate:self];
    [deviceListOutlet setDataSource:self];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (IBAction)discoveryButton:(id)sender
{
    [UIApplication sharedApplication].networkActivityIndicatorVisible = TRUE;
    [DeviceList reset];
    [discoveryOutlet
        setTitle:NSLocalizedString(@"DD_TEXT_SEARCHING", @"DD_TEXT_SEARCHING")
        forState:UIControlStateNormal];
    [discoveryOutlet setEnabled:NO];
    [deviceListOutlet reloadData];
    SampleDeviceDiscovery *deviceDiscovery =
        [[SampleDeviceDiscovery alloc] init];
    [deviceDiscovery performSelectorInBackground:@selector(discover:)
                                      withObject:self];
}

/*
 * Device list table view control functions
 *
 */
- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView
 numberOfRowsInSection:(NSInteger)section
{
    return [DeviceList getSize];
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *MyIdentifier = @"deviceListCell";
    UITableViewCell *cell =
        [tableView dequeueReusableCellWithIdentifier:MyIdentifier];
    if (cell == nil) {
        cell =
            [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                   reuseIdentifier:MyIdentifier];
    }
    DeviceInfo *deviceInfo = [DeviceList getDeviceAt:indexPath.row];
    cell.textLabel.text = [deviceInfo getFriendlyName];
    cell.detailTextLabel.text = [deviceInfo findActionListUrl:@"camera"];

    return cell;
}

- (void)tableView:(UITableView *)tableView
    didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [DeviceList selectDeviceAt:indexPath.row];
    [tableView deselectRowAtIndexPath:[tableView indexPathForSelectedRow]
                             animated:YES];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = TRUE;
}

/**
 * Delegate implementation for receiving device list
 */

- (void)didReceiveDeviceList:(BOOL)isReceived
{
    NSLog(@"SampleDeviceListViewController didReceiveDeviceList: %@",
          isReceived ? @"YES" : @"NO");
    dispatch_async(dispatch_get_main_queue(), ^{
        if (isReceived) {
            [deviceListOutlet reloadData];
        }
        [discoveryOutlet
            setTitle:NSLocalizedString(@"DD_TEXT_START", @"DD_TEXT_START")
            forState:UIControlStateNormal];
        [discoveryOutlet setEnabled:YES];
        [UIApplication sharedApplication].networkActivityIndicatorVisible =
            FALSE;
    });
}

- (BOOL)prefersStatusBarHidden
{
    return NO;
}
@end
