(function() {
  (function($) {
    var getCurrentCheckedStatus;
    getCurrentCheckedStatus = function(checkboxId) {
      console.log('test');
      return $(checkboxId).is(':checked');
    };
    $('#calendar').fullCalendar({
      header: {
        left: 'prev,next today',
        center: 'title',
        right: 'month,basicWeek'
      },
      defaultView: 'month',
      lang: 'de',
      weekends: false,
      eventLimit: true,
      weekNumbers: true,
      eventSources: [
        {
          url: '/calendar',
          data: function() {
            return {
              showApproved: $('#showApproved').is(':checked'),
              showPending: $('#showPending').is(':checked'),
              showRejected: $('#showRejected').is(':checked'),
              showCanceled: $('#showCanceled').is(':checked')
            };
          }
        }
      ],
      eventClick: function(calEvent, jsEvent, view) {
        var $vacationDetail;
        $vacationDetail = $('#vacationDetail');
        $vacationDetail.find('.user').text(calEvent.userFirstName + ' ' + calEvent.userLastName);
        $vacationDetail.find('.state').text(calEvent.state);
        $vacationDetail.find('.time').text(calEvent.start.format('DD.MM.YYYY') + ' - ' + calEvent.end.format('DD.MM.YYYY'));
        $vacationDetail.find('.substitue').text(calEvent.substituteFirstName + ' ' + calEvent.substituteLastName);
        $vacationDetail.show();
        return $('#sidebar').addClass('active');
      }
    });
    return $('#calendarFilter .checkbox input').on('click', function() {
      return $('#calendar').fullCalendar('refetchEvents');
    });
  })(jQuery);

}).call(this);

(function() {
  var refreshVacationForm;

  refreshVacationForm = function(data) {
    var $myForm;
    $myForm = $('#vacation-form-panel');
    $myForm.html(data).hide().fadeIn(800);
    $('.panel-default').matchHeight();
    $myForm.find('.input-group.date').datepicker({});
    return $myForm.find('select').select2();
  };

  (function($) {
    $('.input-group.date').datepicker({
      "format": "dd.mm.yyyy"
    });
    $('.panel-default select').select2();
    $('.autosubmit').on('change', function() {
      return $(this).closest('form').submit();
    });
    $('.panel-default').matchHeight();
    $('[data-toggle="filter"]').click(function() {
      return $('.offcanvas-filter').toggleClass('active');
    });
    return $('.vacation-list a, .task-list a').click(function() {
      $.ajax({
        url: $(this).attr('href'),
        dataType: "html",
        error: function(jqXHR, textStatus, errorThrown) {
          console.log(textStatus);
          return refreshVacationForm('<div><h1>TEST</h1> <div class="input-group date"> <input type="text" class="form-control" name="dateFrom" id="dateFrom" /> <span class="input-group-addon"><i class="glyphicon glyphicon-th"></i></span> </div><div class="col-xs-12 form-group has-error"> <label for="superior">Zuständiger Vorgesetzter *</label> <select class="form-control" style="width: 100%;" name="superior" id="superior"> <option value="oba">Barboza, Omar</option> <option value="sku">Kubiak, Sven</option> <option value="ppl">Plewa, Pascal</option> <option value="bpr">Prenger, Björn</option> <option value="czi">Zillmann, Claudine</option> </select> </div></div>');
        },
        success: function(data) {
          return refreshVacationForm(data);
        }
      });
      return false;
    });
  })(jQuery);

}).call(this);
