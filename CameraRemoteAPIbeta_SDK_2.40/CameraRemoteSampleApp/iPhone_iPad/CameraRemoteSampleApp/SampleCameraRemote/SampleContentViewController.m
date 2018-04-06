/**
 * @file  SampleContentViewController.m
 * @brief CameraRemoteSampleApp
 *
 * Copyright 2014 Sony Corporation
 */

#import "SampleContentViewController.h"
#import "SampleAvContentApi.h"

@implementation SampleContentViewController {
    SampleStreamingDataManager *_streamingDataManager;
    NSURLSession *_session;
}

@synthesize imageView = _imageView;
@synthesize contentFileName = _contentFileName;
@synthesize contentUri = _contentUri;
@synthesize contentUrl = _contentUrl;
@synthesize contentKind = _contentKind;

- (void)viewDidLoad
{
    [super viewDidLoad];
    [UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
}

- (void)viewDidAppear:(BOOL)animated
{
    [super viewDidAppear:animated];
    self.navigationItem.title = _contentFileName;
    if ([_contentKind isEqualToString:@"movie_mp4"] ||
        [_contentKind isEqualToString:@"movie_xavcs"]) {
        _streamingDataManager = [[SampleStreamingDataManager alloc] init];
        [SampleAvContentApi setStreamingContent:self uri:_contentUri];
    } else if ([_contentKind isEqualToString:@"still"]) {
        [self fetchImage:_contentUrl];
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewDidDisappear:(BOOL)animated
{
    [super viewDidDisappear:animated];
    if ([_contentKind isEqualToString:@"movie_mp4"] ||
        [_contentKind isEqualToString:@"movie_xavcs"]) {
        [SampleAvContentApi stopStreaming:self];
        [_streamingDataManager stop];
    } else if ([_contentKind isEqualToString:@"still"]) {
        [_session finishTasksAndInvalidate];
    }
}

/**
 * Download image from the request URL
 */
- (void)fetchImage:(NSString *)requestURL
{
    NSLog(@"SampleContentViewController download URL = %@", requestURL);
    NSURL *downloadUrl = [NSURL URLWithString:requestURL];
    NSURLSessionConfiguration *config =
        [NSURLSessionConfiguration ephemeralSessionConfiguration];
    _session = [NSURLSession sessionWithConfiguration:config];
    NSURLSessionDataTask *task =
        [_session dataTaskWithURL:downloadUrl
                completionHandler:^(NSData *data, NSURLResponse *response,
                                    NSError *error) {
                    [UIApplication sharedApplication]
                        .networkActivityIndicatorVisible = NO;
                    if (data != nil) {
                        dispatch_sync(dispatch_get_main_queue(), ^{
                            UIImage *imageToPost = [UIImage imageWithData:data];
                            [self paintScaledImage:imageToPost];
                        });
                    } else {
                        NSLog(@"SampleContentViewController data object could "
                              @"not be created " @"from download URL = %@",
                              requestURL);
                        [self openNetworkErrorDialog];
                    }
                    [_session finishTasksAndInvalidate];
                }];
    [task resume];
}

/*
 * get the scale of the image with regard to the screen size
 */
- (float)getScale:(CGSize)imageSize
{
    NSInteger imageHeight = imageSize.height;
    NSInteger imageWidth = imageSize.width;
    float hRatio = imageHeight / self.view.frame.size.height;
    float wRatio = imageWidth / self.view.frame.size.width;
    if (hRatio > wRatio) {
        return hRatio;
    } else {
        return wRatio;
    }
}

/**
 * SampleStreamingDataDelegate implementation
 */
- (void)didFetchImage:(UIImage *)image
{
    [self paintScaledImage:image];
}

- (void)didStreamingStopped
{
    [self openNetworkErrorDialog];
}

- (void)paintScaledImage:(UIImage *)image
{
    float scale = [self getScale:image.size];
    UIImage *scaledImage = [UIImage imageWithCGImage:image.CGImage
                                               scale:scale
                                         orientation:image.imageOrientation];
    [_imageView setFrame:CGRectMake(0, 0, scaledImage.size.width,
                                    scaledImage.size.height)];
    [_imageView setCenter:CGPointMake(CGRectGetMidX(self.view.bounds),
                                      CGRectGetMidY(self.view.bounds))];
    [self paintImage:image];
}

- (void)paintImage:(UIImage *)image
{
    [_imageView setImage:image];
    image = NULL;
}

/*
 * Parser of setStreamingContent response
 */
- (void)parseSetStreamingContent:(NSArray *)resultArray
                       errorCode:(NSInteger)errorCode
                    errorMessage:(NSString *)errorMessage
{
    if (resultArray.count > 0 && errorCode < 0) {
        NSDictionary *result = resultArray[0];
        NSLog(@"SampleContentViewController parseSetStreamingContent = %@",
              result[@"playbackUrl"]);
        if (result[@"playbackUrl"] != NULL) {
            _contentUrl = result[@"playbackUrl"];
            [SampleAvContentApi startStreaming:self];
        }
    } else {
        [self openNetworkErrorDialog];
    }
}

/*
 * Parser of startStreaming response
 */
- (void)parseStartStreaming:(NSArray *)resultArray
                  errorCode:(NSInteger)errorCode
               errorMessage:(NSString *)errorMessage
{
    NSLog(@"SampleContentViewController parseStartStreaming");
    if (errorCode < 0 && _contentUrl != nil) {
        [_streamingDataManager start:_contentUrl viewDelegate:self];
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
    NSLog(@"SampleContentViewController parseMessage = %@ apiName = %@",
          responseText, apiName);

    NSError *e;
    NSDictionary *dict =
        [NSJSONSerialization JSONObjectWithData:response
                                        options:NSJSONReadingMutableContainers
                                          error:&e];
    if (e) {
        NSLog(@"SampleContentViewController parseMessage error parsing JSON "
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
        NSLog(@"SampleContentViewController parseMessage API=%@, "
              @"errorCode=%ld, errorMessage=%@",
              apiName, (long)errorCode, errorMessage);
    }

    if ([apiName isEqualToString:API_AVCONTENT_setStreamingContent]) {
        [self parseSetStreamingContent:resultArray
                             errorCode:errorCode
                          errorMessage:errorMessage];
    }
    if ([apiName isEqualToString:API_AVCONTENT_startStreaming]) {
        [self parseStartStreaming:resultArray
                        errorCode:errorCode
                     errorMessage:errorMessage];
        [UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
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
