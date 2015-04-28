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
    $('.vacation-list a, .task-list a').click(function() {
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
    return $('#calendar').fullCalendar({
      header: {
        left: 'prev,next today',
        center: 'title',
        right: 'month,basicWeek'
      },
      defaultView: 'month',
      lang: 'de',
      weekends: false,
      eventLimit: true,
      events: [
        {
          title: 'Urlaub bpr',
          status: 'pending',
          start: '2015-04-01',
          end: '2015-04-08'
        }
      ]
    });
  })(jQuery);

}).call(this);
