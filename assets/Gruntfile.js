module.exports = function(grunt) {
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		sass: {
			dist: {
				files: {
					'../src/main/resources/static/css/style.css' : 'sass/style.scss'
				}
			}
		},
		/* jshint: {
      		myFiles: ['js/*.js']
      	}, */
		watch: {
			css: {
				files: 'sass/**/*.scss',
				tasks: ['sass']
			} /*,
			js: {
				files: ['js/*.js'],
                tasks: ['jshint']
            } */
		}
	});
	grunt.loadNpmTasks('grunt-contrib-sass');
	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.loadNpmTasks('grunt-contrib-jshint');
	grunt.registerTask('default',['watch']);
}