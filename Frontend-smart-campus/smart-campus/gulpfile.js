require('gulp').task('perf-tool', function () {
	var options = {
    	siteURL:'http://127.0.0.1:8887',
        sitePages: ['/']
	};
	return require('devbridge-perf-tool').performance(options);
});