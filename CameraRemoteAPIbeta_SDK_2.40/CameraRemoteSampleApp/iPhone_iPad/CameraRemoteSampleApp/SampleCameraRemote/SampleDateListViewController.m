/**
 * @file  SampleDateListViewController.m
 * @brief CameraRemoteSampleApp
 *
 * Copyright 2014 Sony Corporation
 */

#import "SampleDateListViewController.h"
#import "SampleCameraApi.h"
#import "SampleAvContentApi.h"
#import "SampleGalleryViewController.h"

@implementation SampleDateListViewController {
    SampleCameraEventObserver *_eventObserver;
    NSMutableArray *_date_title;
    NSMutableArray *_date_uri;
    BOOL _isAlreadyRequestedContentsTransfer;
}

@synthesize dateList = _dateList;
@synthesize isMovieAvailable = _isMovieAvailable;

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = TRUE;
    _isAlreadyRequestedContentsTransfer = NO;
    _date_title = [[NSMutableArray alloc] init];
    _date_uri = [[NSMutableArray alloc] init];
    [_dateList setDelegate:self];
    [_dateList setDataSource:self];
    [_dateList reloadData];
    _eventObserver = [SampleCameraEventObserver getInstance];
    [_eventObserver startWithDelegate:self];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidDisappear:(BOOL)animated
{
    _isAlreadyRequestedContentsTransfer = NO;
    [super viewDidDisappear:animated];
    [_eventObserver stop];
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
    return [_date_title count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *MyIdentifier = @"dateListCell";
    UITableViewCell *cell =
        [tableView dequeueReusableCellWithIdentifier:MyIdentifier];
    if (cell == nil) {
        cell =
            [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                   reuseIdentifier:MyIdentifier];
    }
    cell.textLabel.text = _date_title[indexPath.row];

    return cell;
}

- (void)tableView:(UITableView *)tableView
    didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    [tableView deselectRowAtIndexPath:[tableView indexPathForSelectedRow]
                             animated:YES];
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSIndexPath *indexPath = [_dateList indexPathForSelectedRow];
    NSLog(@"SampleDateListViewController prepareForSegue = %ld",
          (long)indexPath.row);
    SampleGalleryViewController *viewController =
        [segue destinationViewController];
    viewController.dateUri = _date_uri[indexPath.row];
    viewController.dateTitle = _date_title[indexPath.row];
    viewController.isMovieAvailable = _isMovieAvailable;
}

/**
 * SampleEventObserverDelegate function implementation
 */

- (void)didCameraStatusChanged:(NSString *)status
{
    NSLog(@"SampleDateListViewController didCameraStatusChanged = %@", status);
    if ([status isEqualToString:PARAM_CAMERA_cameraStatus_contentsTransfer]) {
        _isAlreadyRequestedContentsTransfer = NO;
        [SampleAvContentApi getSchemeList:self];
    } else {
        if (_isAlreadyRequestedContentsTransfer == NO) {
            _isAlreadyRequestedContentsTransfer = YES;
            [SampleCameraApi
                setCameraFunction:self
                         function:PARAM_CAMERA_cameraFunction_contentsTransfer];
        }
    }
}

- (void)didFailParseMessageWithError:(NSError *)error
{
    NSLog(@"SampleDateListViewController didFailParseMessageWithError error "
          @"parsing JSON string");
    [self openNetworkErrorDialog];
}

/*
 * Parser of setCameraFunction response
 */
- (void)parseSetCameraFunction:(NSArray *)resultArray
                     errorCode:(NSInteger)errorCode
                  errorMessage:(NSString *)errorMessage
{
    if (resultArray.count > 0 && errorCode < 0) {
        if ([resultArray[0] isKindOfClass:[NSNumber class]]) {
            int result = [(NSNumber *)resultArray[0] intValue];
            NSLog(@"SampleDateListViewController setCameraFunction = %d",
                  result);
        }
    } else {
        // request is refused.
        _isAlreadyRequestedContentsTransfer = NO;
        [self openNetworkErrorDialog];
    }
}

/*
 * Parser of getSchemeList response
 */
- (void)parseGetSchemeList:(NSArray *)resultArray
                 errorCode:(NSInteger)errorCode
              errorMessage:(NSString *)errorMessage
{
    if (resultArray.count > 0 && errorCode < 0) {
        NSArray *result = resultArray[0];
        for (int i = 0; i < result.count; i++) {
            NSDictionary *dict = result[i];
            NSLog(@"SampleDateListViewController parseGetSchemeList = %@",
                  dict[@"scheme"]);
            if ([dict[@"scheme"] isEqualToString:@"storage"]) {
                [SampleAvContentApi getSourceList:self scheme:@"storage"];
                break;
            }
        }
    } else {
        [self openNetworkErrorDialog];
    }
}

/*
 * Parser of getSourceList response
 */
- (void)parseGetSourceList:(NSArray *)resultArray
                 errorCode:(NSInteger)errorCode
              errorMessage:(NSString *)errorMessage
{
    if (resultArray.count > 0 && errorCode < 0) {
        NSArray *result = resultArray[0];
        if (result.count > 0) {
            NSDictionary *dict = result[0];
            NSLog(@"SampleDateListViewController parseGetSourceList = %@",
                  dict[@"source"]);
            if (dict[@"source"] != NULL) {
                [SampleAvContentApi getContentList:self
                                               uri:dict[@"source"]
                                              view:@"date"
                                              type:NULL];
            }
        }

    } else {
        [self openNetworkErrorDialog];
    }
}

/*
 * Parser of getContentList response
 */
- (void)parseGetContentList:(NSArray *)resultArray
                  errorCode:(NSInteger)errorCode
               errorMessage:(NSString *)errorMessage
{
    if (resultArray.count > 0 && errorCode < 0) {
        NSArray *result = resultArray[0];
        _date_title = [[NSMutableArray alloc] init];
        _date_uri = [[NSMutableArray alloc] init];
        for (int i = 0; i < result.count; i++) {
            if (![result[i] isKindOfClass:[NSDictionary class]]) {
                continue;
            }
            NSDictionary *dict = result[i];
            NSLog(@"SampleDateListViewController parseGetContentList = %@",
                  dict[@"uri"]);
            if (![dict[@"isBrowsable"] isKindOfClass:[NSString class]]) {
                continue;
            }
            NSString *isBrowsable = dict[@"isBrowsable"];
            if ([@"true" isEqualToString:isBrowsable]) {
                [_date_title addObject:dict[@"title"]];
                [_date_uri addObject:dict[@"uri"]];
            }
        }
        [UIApplication sharedApplication].networkActivityIndicatorVisible =
            FALSE;
        [_dateList reloadData];
    } else {
        [self openNetworkErrorDialog];
    }
}

/*
 * Delegate parser implementation for WebAPI requests
 */
- (void)parseMessage:(NSData *)response apiName:(NSString *)apiName
{
    NSString *responseText =
        [[NSString alloc] initWithData:response encoding:NSUTF8StringEncoding];
    NSLog(@"SampleDateListViewController parseMessage = %@ apiName = %@",
          responseText, apiName);

    NSError *e;
    NSDictionary *dict =
        [NSJSONSerialization JSONObjectWithData:response
                                        options:NSJSONReadingMutableContainers
                                          error:&e];
    if (e) {
        NSLog(@"SampleDateListViewController parseMessage error parsing JSON "
              @"string");
        [self openNetworkErrorDialog];
        return;
    }

    NSArray *resultArray = [[NSArray alloc] init];
    if ([dict[@"result"] isKindOfClass:[NSArray class]]) {
        resultArray = dict[@"result"];
    }

    NSArray *errorArray = nil;
    NSString *errorMessage = @"";
    NSInteger errorCode = -1;
    if ([dict[@"error"] isKindOfClass:[NSArray class]]) {
        errorArray = dict[@"error"];
    }
    if (errorArray != nil && errorArray.count >= 2) {
        errorCode = [(NSNumber *)errorArray[0] intValue];
        errorMessage = errorArray[1];
        NSLog(@"SampleDateListViewController parseMessage API=%@, "
              @"errorCode=%ld, errorMessage=%@",
              apiName, (long)errorCode, errorMessage);
    }

    if ([apiName isEqualToString:API_CAMERA_setCameraFunction]) {
        [self parseSetCameraFunction:resultArray
                           errorCode:errorCode
                        errorMessage:errorMessage];
    }
    if ([apiName isEqualToString:API_AVCONTENT_getSchemeList]) {
        [self parseGetSchemeList:resultArray
                       errorCode:errorCode
                    errorMessage:errorMessage];
    }
    if ([apiName isEqualToString:API_AVCONTENT_getSourceList]) {
        [self parseGetSourceList:resultArray
                       errorCode:errorCode
                    errorMessage:errorMessage];
    }
    if ([apiName isEqualToString:API_AVCONTENT_getContentList]) {
        [self parseGetContentList:resultArray
                        errorCode:errorCode
                     errorMessage:errorMessage];
    }
}

- (void)openNetworkErrorDialog
{
    UIAlertView *alert = [[UIAlertView alloc]
            initWithTitle:NSLocalizedString(@"NETWORK_ERROR_HEADING",
                                            @"NETWORK_ERROR_HEADING")
                  message:NSLocalizedString(@"NETWORK_ERROR_MESSAGE",
                                            @"NETWORK_ERROR_MESSAGE")
                 delegate:nil
        cancelButtonTitle:@"OK"
        otherButtonTitles:nil];
    [alert show];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
}

- (BOOL)prefersStatusBarHidden
{
    return NO;
}
@end
