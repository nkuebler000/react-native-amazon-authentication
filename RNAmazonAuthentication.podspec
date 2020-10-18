require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name           = 'RNAmazonAuthentication'
  s.version        = package['version']
  s.summary        = package['description']
  s.description    = package['description']
  s.license        = package['license']
  s.author         = package['author']
  s.homepage       = package['homepage']
  s.platform       = :ios, '10.0'
  s.source         = { :git => '[https://github.com/akhilesh-mourya/react-native-amazon-authentication.git]', :branch => 'master' }
  s.source_files   = "ios/**/*.{h,m}"
  s.dependency "React"

end