    $('.flip').click(function(){

        $('.card').toggleClass('flipped');

        setTimeout(function() {
            $('.card .front').toggleClass('invisible');
            $('.card .back').toggleClass('invisible');
        },180);

    });