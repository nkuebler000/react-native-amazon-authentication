require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name           = 'react-native-amazon-authentication'
  s.version        = package['version']
  s.summary        = package['description']
  s.description    = package['description']
  s.license        = package['license']
  s.author         = package['author']
  s.homepage       = 'https://github.com/akhilesh-mourya/react-native-amazon-authentication'
  s.source       = { :git => "https://github.com/akhilesh-mourya/react-native-amazon-authentication.git", :tag => "#{s.version}" }

  s.ios.deployment_target = "8.0"
  s.tvos.deployment_target = "9.0"

  s.subspec "RNAmazonAuthentication" do |ss|
    ss.source_files  = "ios/RNAmazonAuthentication/*.{h,m}"
    s.static_framework = true
  end

  s.dependency "React-Core"
  s.default_subspec = "RNAmazonAuthentication"

end