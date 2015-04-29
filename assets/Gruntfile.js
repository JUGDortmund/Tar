module.exports = function(grunt) {
	grunt.initConfig({
		pkg: grunt.file.readJSON('package.json'),
		sass: {
			options: {                
        		style: 'compressed'
      		},
			dist: {

				files: {
					'../src/main/resources/static/css/style.css' : 'sass/style.scss'
				}
			}
		},
		coffee: {
			compile: {
				files: {
					'../src/main/resources/static/js/functions.js' : 'coffee/*.coffee'	
				}
			}
		},
		uglify: {
    		my_target: {
		      files: {
		        '../src/main/resources/static/js/functions.min.js': ['../src/main/resources/static/js/functions.js']
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
			},
			coffee: {
				files: 'coffee/**/*.coffee',
				tasks: ['coffee', 'uglify']
			}
		}
	});
	grunt.loadNpmTasks('grunt-contrib-sass');
	grunt.loadNpmTasks('grunt-contrib-coffee');
	grunt.loadNpmTasks('grunt-contrib-uglify');
	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.registerTask('default',['watch']);
}