###
Useful tools - filter and directives
###
angular.module('mediamanager')

#
# Split text and and 'tail' at this end
#
.filter 'cut', ->
  (value, wordwise, max, tail) ->
    if value?
      max = parseInt(max, 10);
      if (!max || value.length <= max)
        value;
      else
        value = value.substr(0, max);
        if (wordwise)
          lastspace = value.lastIndexOf(' ')
          if (lastspace != -1)
            value = value.substr(0, lastspace)

        value + (tail || ' …')
    else
      ''

#
# Replace image by another one if not found
#
.directive 'errSrc', ->
  #return
  link: (scope, element, attrs) ->
    element.bind 'error', ->
      element.attr 'src', attrs.errSrc

#
# Get basename from given string (after last /)
#
.filter 'basename', ->
  (value) ->
    index = value.lastIndexOf('/')
    if index == -1 then value else value.substr(index + 1)

#
# Handle a simple click when double click can be involved.
#
.directive 'sglclick', ['$parse', ($parse) ->
  restrict: 'A',
  link: (scope, element, attr) ->
    fn = $parse attr.sglclick
    delay = 300
    clicks = 0
    timer = null

    element.on 'click', (event) ->
      clicks++; #count clicks
      if clicks == 1
        timer = setTimeout ->
          scope.$apply ->
            fn scope, { $event: event };
          clicks = 0             #after action performed, reset counter
        , delay

      else
        clearTimeout timer # prevent single-click action
        clicks = 0           #after action performed, reset counter
]

###
Directive and filters to extract all tags from media
###
angular.module('mediamanager')

# Extract genres, with link for search
.directive 'genres', ->
  #return
  restrict: 'E'
  scope:
    genres: '='
  template: '<span ng-repeat="g in genres" >' +
    '<a ui-sref="medias.list({genres : g})">{{g}}</a><span ng-hide="$last">,</span> </span>' +
    '<a class="icon-search child-hover" title="Search similar" ui-sref="medias.list({genres : genres})"/>'

# Media tags: video qualities, length, type
.directive 'tags', ->
  #return
  restrict: 'E'
  scope:
    media: '='
    vote: '@'
  templateUrl: '/views/directives/tags.html'
  link: (scope, attr) ->
    attr.vote = attr.vote || false;

# Video tag: quality
.filter 'videoTags', ->
  (input, tail) ->
    res = ""
    if input?.length > 0
      for video in input
        quality = if video.quality? then " <span class='quality'>#{video.quality}</span>" else ""
        lang = if video.quality? then " <span class='quality'>#{video.lang}</span>" else ""
        res = "#{res}#{lang}#{quality}"

    if res.length > 0 then res + (tail || "") else res

# Add a play button based on video file available
.directive 'playButton', ->
  #return
  restrict: 'E'
  scope:
    media: '='
    btnSize: '@'
  templateUrl: '/views/directives/play.html'

###
Direct display of some media properties
###
angular.module('mediamanager')

#
# Title with date if any
#
.filter 'mediatitle', ($filter) ->
  (input) ->
    if input?
      dateStr = if input.release? then " (#{$filter('date')(input.release, 'yyyy')})" else ""
      "#{input.title}#{dateStr}"
    else ""

#
# List of person names
#
.filter 'person', ->
  (input, withLink) ->
    res = ""
    if input?.length > 0
      for p in input
        if res.length > 0
          res += ", "
        if withLink
          res += "<a href='/search/crew/#{p.id}'>"
        res += "#{p.name}"
        if withLink
          res += "</a>"

    res

#
# Wrapper for filter
#
.directive 'personList', ->
  restrict: 'E'
  template: '<span class="persons" ng-bind-html="persons | limitTo:getLimit() | person:withLink"></span>'
  scope:
    persons: '='
    withLink: '@'
    limit: '@'
  link: ($scope)->
    $scope.getLimit = ->
      if $scope.limit then $scope.limit else $scope.persons?.length

#
# Img element for poster, if any, else choose no poster img with width 92 or 185.
#
.directive 'poster', ['$interval', 'dateFilter', ($interval, dateFilter) ->
  restrict: 'E'
  template: '<img ng-src="/{{poster()}}?size={{size}}" err-src="{{noPoster}}" />'
  scope:
    url: '='
    size: '@'
  link: ($scope) ->
    # If poster isn't defined
    width = if $scope.size == "MINI" then "92" else "185"
    $scope.noPoster = "img/no-poster-w#{width}.jpg"

    # Poster url
    $scope.poster = ->
      if $scope.url? then $scope.url else $scope.noPoster
]

.directive 'checkList', ->
  #return
  scope:
    list: '=checkList'
    value: '@'
  link: (scope, elem, attrs) ->
    handler = (setup) ->
      checked = elem.prop('checked')
      index = scope.list.indexOf(scope.value)

      if (checked && index == -1)
        if (setup) elem.prop('checked', false)
        else scope.list.push(scope.value)
      else if (!checked && index != -1)
        if (setup) elem.prop('checked', true)
        else scope.list.splice(index, 1)

    setupHandler = handler.bind(null, true)
    changeHandler = handler.bind(null, false)

    elem.bind 'change', ->
      scope.$apply changeHandler
      scope.$watch 'list', setupHandler, true

.directive 'timeprogressbar', ->
  link = (scope, element, attrs) ->
    # Functions...
    updateProgression = ->
      scope.progression = Math.round(100 * scope.position / scope.length);

    # TODO Reenable this...
    #    scope.$watch attrs.position, (value) ->
    #      scope.position = value;
    #      updateProgression()

    #    scope.$watch attrs.player, (value) ->
    #      scope.player = value

    # Set values from attributes
    scope.length = attrs.length;
    scope.position = attrs.position;
    scope.player = attrs.player;

    updateProgression()

  {
  restrict: 'E'
  link: link
  scope:
    status: "="
  template: '<div class="progress" ng-show="status.length">' +
#    '       <span style="position:absolute">{{status.position | time}} / {{status.length | time}}</span>' +
    '<div class="progress-bar progress-bar-striped progress-bar-info active" role="progressbar" style="width: {{status.position | progress:status.length}}%;"
                                              aria-valuenow="{{status.position | progress:status.length}}" aria-valuemin="0" aria-valuemax="100">{{status.position | time}} / {{status.length | time}}</div>' +
    '</div>'
  }

#
# Make element height same than window height - 50px (top navbar size)
#
.directive 'fullHeight', ($window, Window) ->
  restrict: 'A'
  link: (scope, element, attr) ->
    attr.fullHeight = parseInt(attr.fullHeight || 0) + 50

    setHeight = ->
      if Window.getRange() == 'small'
        element.css 'height', 'auto'
      else
        element.css 'height', "#{$window.innerHeight - attr.fullHeight}px"

    $(window).resize ->
      setHeight()

    setHeight()

    Window.register scope, setHeight, false

.factory 'Window', ($window) ->
  toRange = (val) ->
    if (val >= 1800)
      return 'huge'
    else if val >= 1300
      return 'xlarge'
    else if val >= 992
      return 'large'
    else if val >= 768
      return 'medium'
    else
      return 'small'

  safeApply = (scope, fn = null) ->
    phase = scope.$root?.$$phase
    if phase == '$apply' || phase == '$digest'
      fn() if fn && typeof(fn) == 'function'
    else
      scope.$apply fn

  register: (scope, fct, init = true) ->
    $(window).resize ->
      fct toRange($window.innerWidth)
      safeApply scope

    # Initialisation call
    fct toRange($window.innerWidth) if init

  getRange: () ->
    toRange $window.innerWidth

#
# File and dir tree
#
.directive 'fileTree', ($window, Paths) ->
  restrict: 'E'
  scope:
    roots: '='      # Root path to load and extends
    callBack: '='   # Function to call when submit button has been clicked
    indication: '@' # Indication text
    files: '@'      # show files
    small: '@'      # boolean: group buttons instead of having them on right column
  templateUrl: '/views/directives/filetree.html'
  link: (scope, elem, attr) ->
    attr.small ?= false
    if $window.innerWidth < 768
      attr.small = true

  controller: ($scope) ->
    ###
    Tree controls
    ###
    $scope.treeCtrl = {}

    $scope.select = (elem) ->
      if !elem.data?.metadata
        console.log "Element #{elem.data.path} has been selected"
    $scope.valid = ->
      selected = $scope.treeCtrl.get_selected_branch()
      if $scope.callBack? && selected? && !selected.data?.metadata && selected.data?.path
        $scope.callBack selected.data.path

    $scope.expendLevel = 0

    ###
    Tree feeding
    ###

    #
    # Convert element to UI model
    #
    createNode = (elem, path) ->
      node =
        label: elem.name
        children: []
        data:
          path: path
          lazy: elem.lazy

      if elem.isDirectory && (!elem.children? || elem.children.length == 0)
        node.children.push
          label: " empty "
          treeIcon: (b) ->
            "glyphicon glyphicon-info-sign"
          data:
            metadata: true

      node

    #
    # Add element to it's parent node, use the one already existing if any
    #
    addElement = (parent, child, parentPath) ->
      path = "#{parentPath}/#{child.name}"

      if !parent.children
        parent.children = []

      node = findInTree parent.children, child.name
      if !node
        # Node not found, create it
        node = createNode child, path
        parent.children.push node

        # If Lazy -> special behavior to load data
        if child.lazy
          node.children.push
            label: 'loading....'
            treeIcon: (b) ->
              "glyphicon glyphicon-info-sign"
            data:
              metadata: true

          node.onSelect = (node) ->
            Paths.list {paths: [node.data.path]}, (val) ->
              mergeTree val, $scope.tree

              # remove metadata...
              node.children = node.children.filter (n) ->
                !n.data.metadata
              # Add 'empty' indicator if empty
              if node.children?.length == 0
                node.children.push
                  label: " empty "
                  treeIcon: (b) ->
                    "glyphicon glyphicon-info-sign"
                  data:
                    metadata: true

      else
        # Clean node if from its lazy behavior
        if node.data?.notInitialised
          delete node.treeIcon
          delete node.onSelect
        if node.data?.lazy && !child.lazy
          # Not lazy anymore -> remove metadata and specia behavior
          delete node.onSelect
          node.children = node.children.filter (n) ->
            !n.data.metadata

      # Add children
      if child.children?.length
        for c in child.children
          addElement node, c, path


    #
    # Find node based on it's label
    #
    findInTree = (tree, nodeLabel) ->
      node = null
      for e in tree
        if e.label == nodeLabel
          node = e

      node

    #
    # Merge result from REST service to existing tree
    #
    mergeTree = (serverTree, tree = []) ->
      if !is_empty(serverTree)
        for elem in serverTree
          if elem.name.substring(1).indexOf('/') > -1
            # If root is a path and not single folder, split it to 'lazy above nodes'
            lastNode = null
            fullPath = ""

            # For each part of path, create a 'lazy above node'
            split = elem.name.split '/'
            for i in [0..split.length - 2]
              p = split[i]
              label = p
              if !!p
                fullPath += "/#{p}"
              else
                label = '/'
                fullPath = ""

              # Try to find this node is existing tree
              treeToMerge = if lastNode?.children then lastNode.children else tree
              node = findInTree treeToMerge, label

              if !node?
                # If it doesn't exist, create it
                node =
                  label: label
                  children: []
                  data:
                    path: fullPath
                    notInitialised: true
                  treeIcon: (branch) ->
                    "glyphicon glyphicon-folder-close"
                  onSelect: (node) ->
                    Paths.above {path: elem.fullname}, (val) ->
                      mergeTree [val], tree

                if lastNode?
                  lastNode.children.push node
                else
                  tree.push node

              lastNode = node

            # Then, add the last element as classical element with its children - warn, name is changed!
            elem.fullname = elem.name
            elem.name = split[split.length - 1]
            addElement lastNode, elem, fullPath

          else
            # We have full detail
            fakeParent =
              children: tree
            addElement fakeParent, elem, ""

      tree

    ###
    Initial load
    ###
    $scope.tree = []

    #
    # Update tree when roots are changed
    #
    $scope.$watch 'roots', (roots, oldRoots) ->
      if roots?
        # Reset and Load tree
        $scope.tree = []
        Paths.list {paths: roots}, (val) ->
          mergeTree val, $scope.tree

          # Expends tree to expect tree or to the first path in response
          roots = [val[0].fullname] if roots?.length == 0 && val?.length > 0

          for root in roots
            prevList = $scope.tree
            for nodeName in root.split('/')
              if prevList?
                nodeName = if !!nodeName then nodeName else '/'
                node = findInTree prevList, nodeName
                node?.expanded = true
                prevList = node?.children
      else
        $scope.tree = []


.filter 'rate', ($filter) ->
  (input, votes) ->
    out = if !input || input == 0 then "" else $filter('number')(input * 100.0, 0) + " %"

    if (votes)
      out += " (" + votes + " votes)"

    out

.filter 'progress', ->
  (input, length) ->
    Math.round(100 * input / length)

.filter 'time', ->
  minutes = (sec, force) ->
    mins = (sec % 3600) // 60;
    if mins > 0 || force then "#{mins}' " else ""

  hours = (seconds) ->
    h = seconds // 3600
    if h > 0 then "#{h}h " else ""

  (input) ->
    if input?
      sec = if input % 60 == 0 then "" else "#{input % 60}\""
      "#{hours input}#{minutes input, input > 3600}#{sec}"
    else
      ""


.filter 'mediaType', ->
  (input, types) ->
    type = null
    for t in types
      if t.value == input
        type = t

    if type?
      "<i class='glyphicon glyphicon-#{type.icon}' title='#{type.name}'></i> <span class='hidden-xs'>#{type.name}</span>"
    else
      input

#
# Button set to act on order (in movie filter for example)
#
.directive 'sort', ($window, Paths) ->
  restrict: 'E'
  scope:
    filter: '='      # Read-Write value
    onChange: '='   # Fonction called when a value is changed
  templateUrl: '/views/directives/sort.html'
  controller: ($scope) ->
    $scope.toogleOrder = (options...) ->
      index = options.indexOf($scope.filter.order)
      $scope.filter.order = options[(index + 1) % options.length]

      # Fire value changed
      $scope.onChange($scope.filter) if $scope.onChange

###
Direct useful tools
###
typeIsArray = Array.isArray || (value) -> return {}.toString.call(value) is '[object Array]'

is_empty = (obj) ->
  return true if not obj? or obj.length is 0

  if typeIsArray obj
    for o in obj
      return false if !is_empty o
    return true

  return false if obj.length? and obj.length > 0

  for key of obj
    return false if Object.prototype.hasOwnProperty.call(obj, key)

  return true
