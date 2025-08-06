// 모달 기능
function openInstanceModal() {
    document.getElementById('instanceModal').style.display = 'block';
}

function closeInstanceModal() {
    document.getElementById('instanceModal').style.display = 'none';
}

// 탭 기능
document.addEventListener('DOMContentLoaded', function() {
    const tabButtons = document.querySelectorAll('.tab-button');
    const tabContents = document.querySelectorAll('.tab-content');
    
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            const tabName = this.getAttribute('data-tab');
            
            // 모든 탭 버튼에서 active 클래스 제거
            tabButtons.forEach(btn => btn.classList.remove('active'));
            // 모든 탭 콘텐츠에서 active 클래스 제거
            tabContents.forEach(content => content.classList.remove('active'));
            
            // 클릭된 탭 버튼에 active 클래스 추가
            this.classList.add('active');
            // 해당 탭 콘텐츠에 active 클래스 추가
            document.getElementById(tabName + '-tab').classList.add('active');
        });
    });

    // 모달 이벤트 리스너
    const modal = document.getElementById('instanceModal');
    const closeBtn = document.querySelector('.close');
    const instanceItems = document.querySelectorAll('.instance-item');
    const instanceInput = document.getElementById('instanceInput');
    const instanceSearch = document.getElementById('instanceSearch');

    // 모달 닫기 (요소가 존재할 때만)
    if (closeBtn) {
        closeBtn.onclick = closeInstanceModal;
    }
    window.onclick = function(event) {
        if (event.target == modal) {
            closeInstanceModal();
        }
    }

    // 인스턴스 선택 (요소가 존재할 때만)
    if (instanceItems.length > 0) {
        instanceItems.forEach(item => {
            item.addEventListener('click', function() {
            const value = this.getAttribute('data-value');
            const virtualAddress = this.getAttribute('data-virtual-address');
            const longitude = this.getAttribute('data-longitude');
            const latitude = this.getAttribute('data-latitude');
            const text = this.textContent;
            
            console.log('Selected values:', {
                value: value,
                virtualAddress: virtualAddress,
                longitude: longitude,
                latitude: latitude,
                text: text
            });
            
            // instanceInput에는 표시용 텍스트 설정 (화면 표시용)
            instanceInput.value = text;
            instanceInput.setAttribute('data-selected-value', value);
            instanceInput.setAttribute('data-virtual-address', virtualAddress);
            instanceInput.setAttribute('data-longitude', longitude);
            instanceInput.setAttribute('data-latitude', latitude);
            
            // virtualAddress 필드에는 실제 주소만 설정 (서버 전송용)
            document.getElementById('virtualAddress').value = virtualAddress;
            document.getElementById('longitude').value = longitude;
            document.getElementById('latitude').value = latitude;
            
            // selectedInstance 필드에 street_number 설정
            document.getElementById('selectedInstance').value = value;
            
            console.log('Set virtualAddress to:', document.getElementById('virtualAddress').value);
            
            closeInstanceModal();
        });
    });
    }

    // 검색 기능 (요소가 존재할 때만)
    if (instanceSearch) {
        instanceSearch.addEventListener('input', function() {
        const searchTerm = this.value.toLowerCase();
        instanceItems.forEach(item => {
            const text = item.textContent.toLowerCase();
            if (text.includes(searchTerm)) {
                item.classList.remove('hidden');
            } else {
                item.classList.add('hidden');
            }
        });
    });
    }
});

// 스크립트 로딩 확인 함수
function waitForKakaoMap(callback) {
    if (window.kakao && window.kakao.maps && window.kakao.maps.services) {
        callback();
    } else {
        setTimeout(function() {
            waitForKakaoMap(callback);
        }, 100);
    }
}

function execDaumPostcode() {
    new daum.Postcode({
        oncomplete: function (data) {
            var addr = '';
            var extraAddr = '';

            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;
            } else {
                addr = data.jibunAddress;
            }

            if (data.userSelectedType === 'R') {
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
//                document.getElementById("daum_extraAddress").value = extraAddr;
//            } else {
//                document.getElementById("daum_extraAddress").value = '';
            }

//            document.getElementById('daum_postcode').value = data.zonecode;
//            document.getElementById("daum_detailAddress").focus();
            document.getElementById("virtualAddress").value = addr;


            // 📍 주소 → 좌표 변환 요청
            getCoordsFromAddress(addr);
        }
    }).open();
}

function getCoordsFromAddress(address) {
    waitForKakaoMap(function() {
        const geocoder = new kakao.maps.services.Geocoder();

        geocoder.addressSearch(address, function(result, status) {
            if (status === kakao.maps.services.Status.OK) {
                const lat = result[0].y;
                const lng = result[0].x;
                document.getElementById("latitude").value = lat;
                document.getElementById("longitude").value = lng;
            } else {
                alert("좌표를 찾을 수 없습니다.");
            }
        });
    });
}

function execDaumPostcode_comment() {
    new daum.Postcode({
        oncomplete: function (data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if (data.userSelectedType === 'R') {
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
                // 조합된 참고항목을 해당 필드에 넣는다.
                document.getElementById("commentAddressExtra").value = extraAddr;
//
//            } else {
                document.getElementById("commentAddressExtra").value = '';
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
             document.getElementById('commentZipcode').value = data.zonecode;
            document.getElementById("commentAddress").value = addr;
            // 커서를 상세주소 필드로 이동한다.
             document.getElementById("commentAddressDetail").focus();
        }
    }).open();
}




function execDaumPostcode_place() {
    new daum.Postcode({
        oncomplete: function (data) {
            // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

            // 각 주소의 노출 규칙에 따라 주소를 조합한다.
            // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
            var addr = ''; // 주소 변수
            var extraAddr = ''; // 참고항목 변수

            //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
            if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                addr = data.roadAddress;
            } else { // 사용자가 지번 주소를 선택했을 경우(J)
                addr = data.jibunAddress;
            }

            // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
            if (data.userSelectedType === 'R') {
                // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
                    extraAddr += data.bname;
                }
                // 건물명이 있고, 공동주택일 경우 추가한다.
                if (data.buildingName !== '' && data.apartment === 'Y') {
                    extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                }
                // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                if (extraAddr !== '') {
                    extraAddr = ' (' + extraAddr + ')';
                }
                // 조합된 참고항목을 해당 필드에 넣는다.
                document.getElementById("placeAddressExtra").value = extraAddr;

            } else {
                document.getElementById("placeAddressExtra").value = '';
            }

            // 우편번호와 주소 정보를 해당 필드에 넣는다.
             document.getElementById('placeZipcode').value = data.zonecode;
            document.getElementById("placeAddress").value = addr;
            // 커서를 상세주소 필드로 이동한다.
             document.getElementById("placeAddressDetail").focus();
        }
    }).open();
}



document.addEventListener("DOMContentLoaded", () => {
    const submitBtn = document.getElementById("submitBtn");
    const textareas = document.querySelectorAll("textarea.notice__form-element");
    const daumAddress = document.getElementById("virtualAddress");
    const instanceInput = document.getElementById("instanceInput");
    const subject = document.getElementById("subject");
    // 새로 추가된 필드들
    const title = document.querySelector('input[name="title"]');
    const placeAddress = document.getElementById("placeAddress");
    const placeAddressDetail = document.getElementById("placeAddressDetail");

    function checkRequiredFields() {
        const allTextareasFilled = Array.from(textareas).every(
            (t) => t.value.trim() !== ""
        );
        const subjectSelected = subject && subject.value.trim() !== "";
        const titleFilled = title && title.value.trim() !== "";
        const placeAddressFilled = placeAddress && placeAddress.value.trim() !== "";
        const placeAddressDetailFilled = placeAddressDetail && placeAddressDetail.value.trim() !== "";

        // 현재 활성화된 탭 확인
        const activeTab = document.querySelector('.tab-button.active');
        const activeTabName = activeTab ? activeTab.getAttribute('data-tab') : 'address';
        
        let addressValid = false;
        if (activeTabName === 'address') {
            // 주소 탭이 활성화된 경우 주소 입력이 필수
            addressValid = daumAddress && daumAddress.value.trim() !== "";
        } else if (activeTabName === 'instance') {
            // 인스턴스 탭이 활성화된 경우 인스턴스 선택이 필수
            addressValid = instanceInput && instanceInput.value.trim() !== "";
        }

        const allValid = allTextareasFilled && addressValid && subjectSelected &&
                        titleFilled && placeAddressFilled && placeAddressDetailFilled;

        if (submitBtn) {
            submitBtn.disabled = !allValid;
            submitBtn.classList.toggle("active", allValid);
        }
    }

    // 이벤트 리스너에도 새 필드들 추가
    [...textareas, daumAddress, instanceInput, subject, title, placeAddress, placeAddressDetail].forEach((el) => {
        if (el) {
            el.addEventListener("input", checkRequiredFields);
            el.addEventListener("change", checkRequiredFields);
        }
    });

    // 탭 전환 시에도 필수 필드 체크 실행
    const tabButtons = document.querySelectorAll('.tab-button');
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            setTimeout(checkRequiredFields, 100); // 탭 전환 후 체크
        });
    });

    checkRequiredFields(); // 초기 체크
});

document.addEventListener("DOMContentLoaded", () => {
  const CLOUD_NAME = "dcf56b3ch";
  const UPLOAD_PRESET = "DNBStoryge";

  const form = document.getElementById("uploadForm");

  // 단일 이미지 관련 요소
  const imageInput = document.getElementById("imageInput");
  const preview = document.getElementById("preview");
  const hiddenSingleUrlInput = document.getElementById("imageUrl");

  // 다중 이미지 관련 요소
  const mainImageInput = document.getElementById("mainImageInput");
  const previewContainer = document.getElementById("mainPreviewContainer");
  const hiddenMultiUrlInput = document.getElementById("mainImageUrls");

  let mainImageFiles = [];
  let singleImageUrl = "";
  let multiImageUrls = [];

  let singleDone = false;
  let multiDone = false;

  //최종 제출 시도
  function tryFinalSubmit() {
    if (singleDone && multiDone) {
      hiddenSingleUrlInput.value = singleImageUrl;
      hiddenMultiUrlInput.value = JSON.stringify(multiImageUrls);
      form.submit();
    }
  }

  //단일 이미지 미리보기 (요소가 존재할 때만)
  if (imageInput) {
    imageInput.addEventListener("change", () => {
      const file = imageInput.files[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = (e) => {
        if (preview) {
          preview.src = e.target.result;
          preview.style.display = "block";
        }
      };
      reader.readAsDataURL(file);
    });
  }

  // 다중 이미지 썸네일 미리보기 (요소가 존재할 때만)
  if (mainImageInput) {
    mainImageInput.addEventListener("change", () => {
    const files = Array.from(mainImageInput.files);

    if (files.length > 8) {
      alert("최대 8장까지 업로드할 수 있습니다.");
      mainImageInput.value = "";
      previewContainer.innerHTML = "";
      return;
    }

    mainImageFiles = files;
    previewContainer.innerHTML = "";

    files.forEach((file) => {
      const reader = new FileReader();
      reader.onload = (e) => {
        const img = document.createElement("img");
        img.src = e.target.result;
        img.style.width = "100%";
        img.style.height = "100px";
        img.style.objectFit = "cover";
        if (previewContainer) {
          previewContainer.appendChild(img);
        }
      };
      reader.readAsDataURL(file);
    });
  });
  }

  //전체 업로드 로직 (단일 + 다중) (폼이 존재할 때만)
  if (form) {
    form.addEventListener("submit", async (e) => {
    e.preventDefault();
    showLoading(); // ✅ 업로드 시작 시 로딩 표시

    // 선택된 탭 확인 (탭이 존재할 때만)
    const activeTab = document.querySelector('.tab-button.active');
    if (activeTab) {
        const activeTabName = activeTab.getAttribute('data-tab');
        
        // 선택되지 않은 탭의 입력 필드 비활성화
        if (activeTabName === 'address') {
          // 주소 탭이 선택된 경우 인스턴스 선택 필드 비활성화
          if (instanceInput) {
              instanceInput.disabled = true;
              instanceInput.value = '';
              instanceInput.removeAttribute('data-selected-value');
          }
        } else if (activeTabName === 'instance') {
          // 인스턴스 탭이 선택된 경우 주소 관련 필드 비활성화
          const longitudeField = document.getElementById('longitude');
          const latitudeField = document.getElementById('latitude');
          if (longitudeField) longitudeField.disabled = true;
          if (latitudeField) latitudeField.disabled = true;
          if (longitudeField) longitudeField.value = '';
          if (latitudeField) latitudeField.value = '';
          
          // 인스턴스 선택된 값으로 모든 주소 정보 설정
          if (instanceInput) {
              const selectedValue = instanceInput.getAttribute('data-selected-value');
              const selectedVirtualAddress = instanceInput.getAttribute('data-virtual-address');
              const selectedLongitude = instanceInput.getAttribute('data-longitude');
              const selectedLatitude = instanceInput.getAttribute('data-latitude');
              
              if (selectedValue) {
                // virtualAddress 필드에는 실제 주소만 설정
                const virtualAddressField = document.getElementById('virtualAddress');
                if (virtualAddressField) virtualAddressField.value = selectedVirtualAddress;
                if (longitudeField) longitudeField.value = selectedLongitude;
                if (latitudeField) latitudeField.value = selectedLatitude;
              }
          }
          
          // 주소 관련 필드를 다시 활성화 (폼 제출을 위해)
          if (longitudeField) longitudeField.disabled = false;
          if (latitudeField) latitudeField.disabled = false;
        }
    }

    singleDone = false;
    multiDone = false;

    // ▶ 단일 업로드
    const singleFile = imageInput.files[0];
    if (!singleFile) {
      singleImageUrl = "";
      singleDone = true;
    } else {
      const formData = new FormData();
      formData.append("file", singleFile);
      formData.append("upload_preset", UPLOAD_PRESET);
      formData.append("folder", "carrot_img");

      try {
        const res = await fetch(`https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`, {
          method: "POST",
          body: formData
        });
        const data = await res.json();
        if (data.secure_url) {
          singleImageUrl = data.secure_url;
        } else {
          throw new Error("단일 이미지 업로드 실패");
        }
      } catch (err) {
        alert("단일 이미지 업로드 실패: " + err.message);
      } finally {
        singleDone = true;
        tryFinalSubmit();
      }
    }

    // ▶ 다중 업로드
    const uploaded = [];
    for (const file of mainImageFiles) {
      const formData = new FormData();
      formData.append("file", file);
      formData.append("upload_preset", UPLOAD_PRESET);
      formData.append("folder", "carrot_img");

      try {
        const res = await fetch(`https://api.cloudinary.com/v1_1/${CLOUD_NAME}/image/upload`, {
          method: "POST",
          body: formData
        });
        const data = await res.json();
        if (data.secure_url) {
          uploaded.push(data.secure_url);
        }
      } catch (err) {
        console.error("다중 이미지 업로드 실패:", err);
      }
    }

    multiImageUrls = uploaded;
    multiDone = true;

    hideLoading(); // ✅ 모든 업로드 완료 후 로딩 숨기기
    tryFinalSubmit();
  });
  }
});

function showLoading() {
  document.getElementById("loadingOverlay").classList.add("show");
}

function hideLoading() {
  document.getElementById("loadingOverlay").classList.remove("show");
}

document.addEventListener("DOMContentLoaded", () => {
    const textareas = document.querySelectorAll('textarea[data-target]');
  
    textareas.forEach((textarea) => {
      const targetId = textarea.getAttribute("data-target");
      const max = textarea.getAttribute("data-max") || textarea.maxLength;
      const counter = document.getElementById(targetId);
  
      const updateCount = () => {
        counter.textContent = `${textarea.value.length} / ${max}`;
      };
  
      textarea.addEventListener("input", updateCount);
      updateCount(); // 초기화
    });
  });

document.addEventListener("DOMContentLoaded", () => {
    const input = document.querySelectorAll('input[data-target]');

    input.forEach((input) => {
      const targetId = input.getAttribute("data-target");
      const max = input.getAttribute("data-max") || input.maxLength;
      const counter = document.getElementById(targetId);

      const updateCount = () => {
        counter.textContent = `${input.value.length} / ${max}`;
      };

      input.addEventListener("input", updateCount);
      updateCount(); // 초기화
    });
  });


