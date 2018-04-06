/**
 * @file  SampleGalleryViewController.m
 * @brief CameraRemoteSampleApp
 *
 * Copyright 2014 Sony Corporation
 */

#import "SampleGalleryViewController.h"
#import "SampleContentCell.h"
#import "SampleCameraApi.h"
#import "SampleAvContentApi.h"
#import "SampleContentViewController.h"

@implementation SampleGalleryViewController {
    NSMutableArray *_contents_fileName;
    NSMutableArray *_contents_kind;
    NSMutableArray *_contents_uri;
    NSMutableArray *_contents_thumbnailUrl;
    NSMutableArray *_contents_largeUrl;
    SampleCameraEventObserver *_eventObserver;
    NSURLSession *_session;
}

@synthesize galleryView = _galleryView;
@synthesize isMovieAvailable = _isMovieAvailable;
@synthesize dateUri = _dateUri;
@synthesize dateTitle = _dateTitle;

- (void)viewDidLoad
{
    [super viewDidLoad];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    _contents_fileName = [[NSMutableArray alloc] init];
    _contents_kind = [[NSMutableArray alloc] init];
    _contents_uri = [[NSMutableArray alloc] init];
    _contents_thumbnailUrl = [[NSMutableArray alloc] init];
    _contents_largeUrl = [[NSMutableArray alloc] init];
    [_galleryView setDelegate:self];
    [_galleryView setDataSource:self];
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    self.navigationItem.title = _dateTitle;

    _eventObserver = [SampleCameraEventObserver getInstance];
    [_eventObserver startWithDelegate:self];

    NSURLSessionConfiguration *config =
        [NSURLSessionConfiguration ephemeralSessionConfiguration];
    _session = [NSURLSession sessionWithConfiguration:config];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidDisappear:(BOOL)animated
{
    [_session finishTasksAndInvalidate];
    [super viewDidDisappear:animated];
    [_eventObserver stop];
}

- (void)fetchContentsList
{
    [SampleAvContentApi
        getContentList:self
                   uri:_dateUri
                  view:@"date"
                  type:
                      @[ @"\"still\"", @"\"movie_mp4\"", @"\"movie_xavcs\"" ]];
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView
{
    return 1;
}

- (NSInteger)collectionView:(UICollectionView *)collectionView
     numberOfItemsInSection:(NSInteger)section
{
    return [_contents_thumbnailUrl count];
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView
                  cellForItemAtIndexPath:(NSIndexPath *)indexPath
{
    NSLog(@"SampleGalleryViewController cellForItemAtIndexPath = %ld",
          (long)indexPath.row);
    SampleContentCell *cell =
        [collectionView dequeueReusableCellWithReuseIdentifier:@"contentCell"
                                                  forIndexPath:indexPath];
    if (!cell) {
        cell = [[SampleContentCell alloc]
            initWithFrame:CGRectMake(0, 0, cell.frame.size.width,
                                     cell.frame.size.height)];
    }
    cell.layer.borderWidth = 1.0f;
    cell.layer.borderColor = [UIColor whiteColor].CGColor;
    NSDictionary *args = @{
        @"requestURL" : _contents_thumbnailUrl[indexPath.row],
        @"imageView" : cell.thumbnailView
    };
    cell.thumbnailView.image = nil;
    [self fetchThumbnail:args];
    if ([_contents_kind[indexPath.row] isEqualToString:@"still"]) {
        cell.contentType.text = @"still";
    } else {
        cell.contentType.text = @"movie";
    }
    return cell;
}

- (BOOL)shouldPerformSegueWithIdentifier:(NSString *)identifier
                                  sender:(id)sender
{
    NSIndexPath *indexPath =
        [self.collectionView indexPathsForSelectedItems][0];
    if (!_isMovieAvailable &&
        ([_contents_kind[indexPath.row] isEqualToString:@"movie_mp4"] ||
         [_contents_kind[indexPath.row] isEqualToString:@"movie_xavcs"])) {
        [self openUnsupportedStreamingErrorDialog];
        return NO;
    }
    return YES;
}

// the user tapped a collection item, load and set the image on the detail view
// controller
//
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSIndexPath *indexPath =
        [self.collectionView indexPathsForSelectedItems][0];
    NSLog(@"SampleGalleryViewController prepareForSegue = %ld",
          (long)indexPath.row);
    SampleContentViewController *viewController =
        [segue destinationViewController];
    viewController.contentFileName = _contents_fileName[indexPath.row];
    viewController.contentKind = _contents_kind[indexPath.row];
    viewController.contentUri = _contents_uri[indexPath.row];
    if ([_contents_kind[indexPath.row] isEqualToString:@"movie_mp4"] ||
        [_contents_kind[indexPath.row] isEqualToString:@"movie_xavcs"]) {
        viewController.contentUrl = nil;
    } else if ([_contents_kind[indexPath.row] isEqualToString:@"still"]) {
        viewController.contentUrl = _contents_largeUrl[indexPath.row];
    }
}

/**
 * Download image from the received URL
 */
- (void)fetchThumbnail:(NSDictionary *)dict
{
    NSString *requestURL = dict[@"requestURL"];
    UIImageView *imageView = dict[@"imageView"];
    NSLog(@"SampleGalleryViewController download URL = %@", requestURL);
    NSURL *downloadUrl = [NSURL URLWithString:requestURL];

    NSURLSessionDataTask *task =
        [_session dataTaskWithURL:downloadUrl
                completionHandler:^(NSData *data, NSURLResponse *response,
                                    NSError *error) {
                    if (data != nil) {
                        dispatch_sync(dispatch_get_main_queue(), ^{
                            UIImage *imageToPost = [UIImage imageWithData:data];
                            [imageView setImage:imageToPost];

                        });
                    } else {
                        NSLog(@"SampleContentViewController data object could "
                              @"not be created " @"from download URL = %@",
                              requestURL);
                        [self openNetworkErrorDialog];
                    }
                }];
    [task resume];
}

/**
 * SampleEventObserverDelegate function implementation
 */

- (void)didCameraStatusChanged:(NSString *)status
{
    NSLog(@"SampleGalleryViewController didCameraStatusChanged = %@", status);
    if ([status isEqualToString:PARAM_CAMERA_cameraStatus_contentsTransfer]) {
        [self fetchContentsList];
    }
}

- (void)didFailParseMessageWithError:(NSError *)error
{
    NSLog(@"SampleGalleryViewController didFailParseMessageWithError error "
          @"parsing JSON string");
    [self openNetworkErrorDialog];
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
        _contents_fileName = [[NSMutableArray alloc] init];
        _contents_kind = [[NSMutableArray alloc] init];
        _contents_uri = [[NSMutableArray alloc] init];
        _contents_thumbnailUrl = [[NSMutableArray alloc] init];
        _contents_largeUrl = [[NSMutableArray alloc] init];
        for (int i = 0; i < result.count; i++) {
            if (![result[i] isKindOfClass:[NSDictionary class]]) {
                continue;
            }
            NSDictionary *dict = result[i];
            NSLog(@"SampleGalleryViewController parseGetContentList = %@",
                  dict[@"uri"]);
            if (![dict[@"isBrowsable"] isKindOfClass:[NSString class]]) {
                continue;
            }
            NSString *isBrowsable = dict[@"isBrowsable"];
            if ([@"false" isEqualToString:isBrowsable]) {
                if (![dict[@"content"] isKindOfClass:[NSDictionary class]]) {
                    continue;
                }
                NSDictionary *content = dict[@"content"];
                if (![content[@"original"] isKindOfClass:[NSArray class]]) {
                    continue;
                }
                NSArray *original = content[@"original"];
                if (original.count > 0) {
                    if (![original[0] isKindOfClass:[NSDictionary class]]) {
                        continue;
                    }
                }
                if (!dict[@"contentKind"] && !dict[@"uri"] &&
                    !original[0][@"fileName"] && !content[@"thumbnailUrl"]) {
                    continue;
                }
                if ([dict[@"contentKind"] isEqualToString:@"still"] &&
                    !content[@"largeUrl"]) {
                    continue;
                }
                [_contents_kind addObject:dict[@"contentKind"]];
                [_contents_uri addObject:dict[@"uri"]];
                [_contents_fileName addObject:original[0][@"fileName"]];
                [_contents_thumbnailUrl addObject:content[@"thumbnailUrl"]];
                if ([dict[@"contentKind"] isEqualToString:@"still"]) {
                    [_contents_largeUrl addObject:content[@"largeUrl"]];
                } else {
                    [_contents_largeUrl addObject:[NSNull null]];
                }
                NSLog(@"SampleGalleryViewController parseGetContentList "
                      @"thumbnail = %@",
                      content[@"thumbnailUrl"]);
            }
        }
        [UIApplication sharedApplication].networkActivityIndicatorVisible =
            FALSE;
        [_galleryView reloadData];
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
    NSLog(@"SampleGalleryViewController parseMessage = %@ apiName = %@",
          responseText, apiName);

    NSError *e;
    NSDictionary *dict =
        [NSJSONSerialization JSONObjectWithData:response
                                        options:NSJSONReadingMutableContainers
                                          error:&e];
    if (e) {
        NSLog(@"SampleGalleryViewController parseMessage error parsing JSON "
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
        errorCode = (NSInteger)errorArray[0];
        errorMessage = errorArray[1];
        NSLog(@"SampleGalleryViewController parseMessage API=%@, "
              @"errorCode=%ld, errorMessage=%@",
              apiName, (long)errorCode, errorMessage);
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

- (void)openUnsupportedStreamingErrorDialog
{
    UIAlertView *alert = [[UIAlertView alloc]
            initWithTitle:NSLocalizedString(@"UNSUPPORTED_STREAMING_HEADING",
                                            @"UNSUPPORTED_STREAMING_HEADING")
                  message:NSLocalizedString(@"UNSUPPORTED_STREAMING_MESSAGE",
                                            @"UNSUPPORTED_STREAMING_MESSAGE")
                 delegate:nil
        cancelButtonTitle:@"OK"
        otherButtonTitles:nil];
    [alert show];
}

- (BOOL)prefersStatusBarHidden
{
    return NO;
}
@end
